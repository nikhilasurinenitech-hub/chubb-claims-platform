import {test,expect,Page} from '@playwright/test';
const password=process.env['DEMO_PASSWORD']||'ClaimsDemo!2026';
async function login(page:Page,user:string){
 await page.goto('/');await page.getByLabel('Demo account').selectOption(user);await page.getByLabel('Password',{exact:true}).fill(password);await page.getByRole('button',{name:'Enter workspace'}).click();await expect(page.getByRole('button',{name:'Sign out'})).toBeVisible();
}
async function create(page:Page){
 await page.getByRole('button',{name:'+ Report an incident',exact:true}).click();
 await page.getByLabel('Incident location').fill('Singapore demo car park');
 await page.getByLabel('What happened?').fill('Browser test: vehicle door damaged while parked.');
 await page.getByLabel('Estimated amount').fill('5000');
 const pending=page.waitForResponse(r=>r.url().endsWith('/api/claims')&&r.request().method()==='POST');
 await page.getByRole('button',{name:'Submit claim',exact:true}).click();
 const response=await pending;expect(response.status()).toBe(201);return (await response.json()).claim;
}
async function openStaffClaim(page:Page,reference:string){
 await page.getByLabel('Search claims').fill(reference);await page.getByRole('button',{name:'Apply',exact:true}).click();
 await page.locator('button.claim-row').filter({hasText:reference}).click();
}
test('claimant and officer complete the information-to-settlement workflow',async({browser})=>{
 const claimant=await browser.newContext(),officer=await browser.newContext();
 const cp=await claimant.newPage(),sp=await officer.newPage();
 await login(cp,'maya');const claim=await create(cp);
 await login(sp,'alex');await openStaffClaim(sp,claim.reference);
 const sd=sp.getByRole('region',{name:'Selected claim'});
 await sd.getByRole('button',{name:'Pick up this claim'}).click();
 await expect(sd.locator('claim-status')).toHaveText('Under review');
 await sd.getByLabel('Assessment, question or decision reason').fill('Please provide the repair estimate.');
 await sd.getByRole('button',{name:'Request information',exact:true}).click();
 await expect(sd.locator('claim-status')).toHaveText('Needs information');
 await cp.getByRole('button',{name:'Refresh',exact:true}).click();
 await cp.locator('button.claim-row').filter({hasText:claim.reference}).click();
 const cd=cp.getByRole('region',{name:'Selected claim'});
 await cd.getByLabel('Your response').fill('Repair estimate is SGD 4000 with panel replacement.');
 await cd.getByRole('button',{name:'Send response'}).click();
 await expect(cd.locator('claim-status')).toHaveText('Under review');
 await sp.getByRole('button',{name:'Refresh workspace'}).click();
 await expect(sd.locator('claim-status')).toHaveText('Under review');
 await sd.getByLabel('Assessment, question or decision reason').fill('Repair estimate reviewed and accepted.');
 await sd.getByLabel('Assessed reserve (SGD)',{exact:true}).fill('4000');
 await sd.getByRole('button',{name:'Save assessment'}).click();
 await expect(sd.getByRole('button',{name:'Approve claim'})).toBeEnabled();
 await sd.getByRole('button',{name:'Approve claim'}).click();
 await sd.getByLabel('Settlement amount').fill('3800');
 await sd.getByLabel('Settlement note').fill('Settlement agreed after estimate review.');
 await sd.getByRole('button',{name:'Confirm settlement'}).click();
 await expect(sd.locator('claim-status')).toHaveText('Settled');
 await cp.getByRole('button',{name:'Refresh',exact:true}).click();
 await cp.locator('button.claim-row').filter({hasText:claim.reference}).click();
 await expect(cd.getByText('Settlement recorded',{exact:true})).toBeVisible();
 await expect(cd.getByText('SGD 3,800.00',{exact:true})).toBeVisible();
 const forbidden=await cp.request.get('/api/claims/'+claim.id,{headers:{Authorization:'Basic '+Buffer.from('leo:'+password).toString('base64')}});
 expect(forbidden.status()).toBe(404);
 await expect.poll(async()=>{const r=await sp.request.get('/api/insights',{headers:{Authorization:'Basic '+Buffer.from('alex:'+password).toString('base64')}});return (await r.json()).byStatus.SETTLED;}).toBeGreaterThan(0);
 await claimant.close();await officer.close();
});
test('officer rejection reaches the claimant with its reason',async({browser})=>{
 const c=await browser.newContext(),s=await browser.newContext(),cp=await c.newPage(),sp=await s.newPage();
 await login(cp,'maya');const claim=await create(cp);await login(sp,'alex');await openStaffClaim(sp,claim.reference);
 const sd=sp.getByRole('region',{name:'Selected claim'});await sd.getByRole('button',{name:'Pick up this claim'}).click();
 await sd.getByLabel('Assessment, question or decision reason').fill('Demo decision: reported loss falls outside the assumed coverage.');
 await sd.getByRole('button',{name:'Reject claim',exact:true}).click();await expect(sd.locator('claim-status')).toHaveText('Rejected');
 await cp.getByRole('button',{name:'Refresh',exact:true}).click();await cp.locator('button.claim-row').filter({hasText:claim.reference}).click();
 await expect(cp.getByRole('region',{name:'Selected claim'}).getByText('Claim rejected',{exact:true})).toBeVisible();
 await c.close();await s.close();
});
test('mobile form validates required fields and stays within the viewport',async({page})=>{
 await page.setViewportSize({width:390,height:844});await login(page,'maya');await page.getByRole('button',{name:'+ Report an incident',exact:true}).click();
 await expect(page.getByRole('button',{name:'Submit claim',exact:true})).toBeDisabled();
 await page.getByLabel('Incident location').fill('Demo location');await page.getByLabel('What happened?').fill('Brief');
 await page.getByLabel('Estimated amount').fill('100');await expect(page.getByRole('button',{name:'Submit claim',exact:true})).toBeDisabled();
 expect(await page.evaluate(()=>document.documentElement.scrollWidth<=window.innerWidth)).toBe(true);
 await page.getByLabel('What happened?').fill('Sufficiently detailed fictional incident.');
 await expect(page.getByRole('button',{name:'Submit claim',exact:true})).toBeEnabled();
});
