# Angular workspace

The shell, claimant and staff applications are separate Native Federation builds in one Angular workspace. The shared source library supplies contracts, identity state, HTTP access and presentation components.

Use Node 24 with the checked-in lockfile:

~~~bash
npm ci
npm test
npm run build
~~~

The production output is in dist/shell/browser, dist/claimant/browser and dist/staff/browser. The root Compose configuration serves them together through Nginx and routes API calls to the Java services. Run the complete application using the repository-root README.

The runtime manifest is projects/shell/public/federation.manifest.json. The shared workspace mapping must remain a singleton so all three builds use the same AuthService and Angular injection tokens.

Tests in projects/shared/src/lib/ui.spec.ts cover UI behaviour and authentication boundaries. The e2e directory contains browser workflows against the complete running system.
