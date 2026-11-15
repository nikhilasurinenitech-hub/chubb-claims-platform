import { initFederation } from '@angular-architects/native-federation';
initFederation('federation.manifest.json', { hostRemoteEntry: { url: './remoteEntry.json' } })
  .then((nf) => import('./bootstrap').then((m) => m.bootstrap(nf)))
  .catch((error) => {
    console.error('Workspace startup failed', error);
    const message = document.createElement('p');
    message.textContent =
      'The workspace could not start. Check the web container and reload this page.';
    document.body.replaceChildren(message);
  });
