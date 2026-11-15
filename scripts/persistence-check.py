#!/usr/bin/env python3
"""Optional disruptive local check: recreate containers, retain volumes, replay Kafka."""
import json,os,subprocess,time,urllib.request
from pathlib import Path
project=Path(__file__).resolve().parents[1]
engine=os.environ.get('CONTAINER_ENGINE','podman')
base_url=os.environ.get('BASE_URL','http://127.0.0.1:8080')
def compose(*args):
 print('STEP', ' '.join(args[:3]),flush=True)
 return subprocess.run([engine,'compose',*args],cwd=project,check=True,capture_output=True,text=True,timeout=180).stdout.strip()
def sql(query):return compose('exec','-T','postgres','psql','-U','postgres','-d','claims_platform','-At','-c',query)
def wait_ready():
 for attempt in range(90):
  try:
   for path in ['web','claims','insights']:
    with urllib.request.urlopen(base_url+'/health/'+path,timeout=3) as r:assert r.status==200
   return
  except Exception:time.sleep(1)
 raise AssertionError('System did not restart')
q_claims="select count(*)||':'||coalesce(md5(string_agg((id,status,version,reserve_amount,settlement_amount)::text,',' order by id)),'empty') from claims.claim"
q_insights="select count(*)||':'||coalesce(md5(string_agg((claim_id,status,aggregate_version,reserve_amount,settlement_amount)::text,',' order by claim_id)),'empty') from insights.claim_projection"
before_claims=sql(q_claims);before_insights=sql(q_insights)
compose('down');compose('up','-d');wait_ready()
assert sql(q_claims)==before_claims,'Claims changed after restart'
for n in range(40):
 if sql(q_insights)==before_insights:break
 time.sleep(1)
else:raise AssertionError('Insights did not survive restart')
config=compose('exec','-T','kafka','/opt/kafka/bin/kafka-configs.sh','--bootstrap-server','localhost:29092','--entity-type','topics','--entity-name','claims.changed.v1','--describe')
assert 'cleanup.policy=compact' in config,config
compose('stop','insights-service')
try:
 compose('exec','-T','kafka','/opt/kafka/bin/kafka-consumer-groups.sh','--bootstrap-server','localhost:29092','--group','insights-v1','--topic','claims.changed.v1','--reset-offsets','--to-earliest','--execute')
finally:compose('start','insights-service')
wait_ready()
for attempt in range(30):
 state=compose('exec','-T','kafka','/opt/kafka/bin/kafka-consumer-groups.sh','--bootstrap-server','localhost:29092','--group','insights-v1','--describe')
 rows=[line.split() for line in state.splitlines() if line.startswith('insights-v1')]
 if rows and all(row[5]=='0' for row in rows):break
 time.sleep(1)
else:raise AssertionError('Consumer did not finish replay: '+state)
assert sql(q_insights)==before_insights,'Replayed events changed projection totals'
result={'result':'PASS','claimsBeforeAndAfter':before_claims,'projectionBeforeAndAfter':before_insights,'compactedTopic':True,'replayLagZero':True}
(project/'artifacts').mkdir(exist_ok=True)
(project/'artifacts/persistence-result.json').write_text(json.dumps(result,indent=2)+'\n')
print(json.dumps(result))
