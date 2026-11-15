#!/usr/bin/env python3
"""Export native images or package portable compiled artifacts for another architecture."""
import argparse,gzip,hashlib,json,shutil,subprocess,tempfile
from pathlib import Path
parser=argparse.ArgumentParser()
parser.add_argument('--engine',choices=['podman','docker'],default='podman')
parser.add_argument('--arch',choices=['arm64','amd64'],required=True)
parser.add_argument('--output-dir',type=Path,default=Path('artifacts/images'))
args=parser.parse_args()
root=Path(__file__).resolve().parents[1]
output=args.output_dir.resolve();output.mkdir(parents=True,exist_ok=True)
version='1.0.0';tag=version+'-'+args.arch
def command(*values,capture=False):
 result=subprocess.run([args.engine,*values],check=True,text=True,capture_output=capture,cwd=root)
 return result.stdout.strip() if capture else ''
source_images={name:'localhost/claimspace/'+name+':'+version for name in ['claims','insights','web']}
targets={name:'localhost/claimspace/'+name+':'+tag for name in source_images}
with tempfile.TemporaryDirectory(prefix='claimspace-release-') as temp:
 context=Path(temp)
 for name,image in source_images.items():
  source_arch=command('image','inspect','--format','{{.Architecture}}',image,capture=True)
  if source_arch==args.arch:
   command('tag',image,targets[name]);continue
  destination=context/name;destination.mkdir()
  container=command('create',image,capture=True)
  try:
   if name=='web':
    (destination/'site').mkdir()
    command('cp',container+':/usr/share/nginx/html/.',str(destination/'site'))
    for filename in ['nginx.conf','nginx-main.conf','container-entrypoint.sh']:shutil.copy2(root/'frontend'/filename,destination/filename)
    dockerfile=root/'deploy/images/web.Dockerfile'
   else:
    command('cp',container+':/app/app.jar',str(destination/'app.jar'))
    command('cp',container+':/app/health/com/example/platform/Healthcheck.class',str(destination/'Healthcheck.class'))
    dockerfile=root/'deploy/images/backend.Dockerfile'
  finally:command('rm',container)
  # No RUN steps: JVM bytecode and browser assets are portable; the base selects the CPU architecture.
  command('build','--platform','linux/'+args.arch,'-t',targets[name],'-f',str(dockerfile),str(destination))
 manifest=[]
 for name,image in targets.items():
  info=json.loads(command('image','inspect',image,capture=True))[0]
  architecture=info.get('Architecture') or info.get('architecture')
  if architecture!=args.arch:raise RuntimeError('Incorrect architecture for '+image)
  manifest.append({'name':name,'tag':image,'architecture':architecture,'imageId':info.get('Id') or info.get('ID')})
 tar_path=Path(temp)/'images.tar'
 if args.engine=='podman':command('save','--format','docker-archive','--multi-image-archive','-o',str(tar_path),*targets.values())
 else:command('save','-o',str(tar_path),*targets.values())
 archive=output/('claimspace-images-linux-'+args.arch+'.tar.gz')
 with tar_path.open('rb') as src,gzip.open(archive,'wb',compresslevel=6) as dst:shutil.copyfileobj(src,dst,16*1024*1024)
 digest=hashlib.file_digest(archive.open('rb'),'sha256').hexdigest()
 (output/(archive.name+'.sha256')).write_text(digest+'  '+archive.name+'\n')
 (output/('images-linux-'+args.arch+'.json')).write_text(json.dumps({'format':'docker-archive+gzip','applicationVersion':version,'architecture':args.arch,'images':manifest,'sha256':digest},indent=2)+'\n')
 print(json.dumps({'archive':str(archive),'bytes':archive.stat().st_size,'sha256':digest},indent=2))
