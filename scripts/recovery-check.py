#!/usr/bin/env python3
"""Optional disruptive local test: pauses this project's Kafka, then restarts it in finally."""
import base64,json,os,subprocess,time,uuid,urllib.request
from pathlib import Path
os.chdir(Path(__file__).resolve().parents[1])
engine=os.environ.get('CONTAINER_ENGINE','podman')
base=os.environ.get('BASE_URL','http://127.0.0.1:8080')
password=os.environ.get('DEMO_PASSWORD','ClaimsDemo!2026')
def api(user,path,body=None):
 headers={'Authorization':'Basic '+base64.b64encode((user+':'+password).encode()).decode()}
 if body is not None:headers.update({'Content-Type':'application/json','Idempotency-Key':str(uuid.uuid4())})
 request=urllib.request.Request(base+path,data=json.dumps(body).encode() if body is not None else None,headers=headers)
 with urllib.request.urlopen(request,timeout=20) as response:return json.load(response)
def compose(*args):
 return subprocess.run([engine,'compose',*args],check=True,capture_output=True,text=True).stdout.strip()
def wait_until(check,seconds=75):
 for attempt in range(seconds):
  try:
   value=check()
   if value:return value
  except Exception:pass
  time.sleep(1)
 raise AssertionError('Recovery condition timed out')
compose('stop','kafka')
try:
 result=api('maya','/api/claims',dict(type='PROPERTY',market='SG',incidentDate='2026-01-01',location='Fictional recovery test',description='Recovery test: fictional water damage recorded while Kafka is stopped',requestedAmount=1500))
 claim_id=str(uuid.UUID(result['claim']['id']))
 assert result['claim']['status']=='SUBMITTED'
 assert api('alex','/api/operations')['pendingEvents']>0
 assert api('maya','/api/claims/'+claim_id)['claim']['id']==claim_id
finally:
 compose('start','kafka')
wait_until(lambda:api('alex','/api/operations')['pendingEvents']==0)
query="select status || ':' || aggregate_version::text from insights.claim_projection where claim_id='"+claim_id+"'"
projection=wait_until(lambda:compose('exec','-T','postgres','psql','-U','postgres','-d','claims_platform','-At','-c',query))
assert projection=='SUBMITTED:0',projection
print(json.dumps(dict(result='PASS',claimId=claim_id,committedWhileKafkaStopped=True,projectionAfterRecovery=projection),indent=2))
