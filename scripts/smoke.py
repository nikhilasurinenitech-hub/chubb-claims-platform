#!/usr/bin/env python3
"""Exercise actual REST, Kafka projection and ownership through the public web URL."""
import base64,json,os,time,uuid,urllib.request,urllib.error
base=os.environ.get('BASE_URL','http://127.0.0.1:8080')
password=os.environ.get('DEMO_PASSWORD','ClaimsDemo!2026')
def request(user,path,body=None,key=None,expected=200):
 headers={'Authorization':'Basic '+base64.b64encode((user+':'+password).encode()).decode()}
 if body is not None:headers['Content-Type']='application/json'
 if key:headers['Idempotency-Key']=key
 req=urllib.request.Request(base+path,data=json.dumps(body).encode() if body is not None else None,headers=headers)
 try:
  with urllib.request.urlopen(req,timeout=20) as response:status=response.status;data=json.load(response)
 except urllib.error.HTTPError as error:status=error.code;data=json.load(error)
 assert status==expected,(path,status,data)
 return data
key=str(uuid.uuid4())
body=dict(type='MOTOR',market='SG',incidentDate='2026-01-01',location='Fictional test location',description='Smoke test: fictional parked vehicle damage',requestedAmount=1000)
d=request('maya','/api/claims',body,key,201)
claim=d['claim'];claim_id=claim['id']
assert request('maya','/api/claims',body,key)['claim']['id']==claim_id
request('leo','/api/claims/'+claim_id,expected=404)
def act(action,note='',amount=None):
 global d
 d=request('alex','/api/claims/'+claim_id+'/'+action,dict(version=d['claim']['version'],note=note,amount=amount))
act('assign')
act('information-requests','Please provide a repair estimate')
info=d['informationRequests'][0]
d=request('maya','/api/claims/'+claim_id+'/information-requests/'+info['id']+'/response',dict(version=d['claim']['version'],answer='Fictional estimate totals SGD 900'))
act('assessment','Repair estimate reviewed',900)
act('approve')
act('settle','Demo settlement recorded',850)
assert d['claim']['status']=='SETTLED'
assert len(d['history'])==7
for attempt in range(45):
 report=request('alex','/api/insights')
 ops=request('alex','/api/operations')
 if ops['pendingEvents']==0 and report['byStatus']['SETTLED']>0:break
 time.sleep(1)
else:raise AssertionError('Kafka projection did not catch up')
request('maya','/api/insights',expected=403)
print(json.dumps(dict(result='PASS',reference=claim['reference'],finalStatus=d['claim']['status'],historyEntries=len(d['history']),pendingEvents=ops['pendingEvents'],currencies=[r['currency'] for r in report['exposure']]),indent=2))
