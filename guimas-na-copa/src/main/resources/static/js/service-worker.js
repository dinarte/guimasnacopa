
/*----------------------------- Site Loader & Popup --------------------*/
self.addEventListener('install', (event) => {
	event.waitUntil(
		caches.open('guimasbet-app-cache').then((cache) => {
			return cache.addAll([
				'/',
				'/login',
				'/js/manifest.json',
				'/img/icons/icon-96x96.png',
			]);
		})
	);
  });
  
  self.addEventListener('fetch', (event) => {
	event.respondWith(
		caches.match(event.request).then((response) => {
			return response || fetch(event.request);
		})
	);
  });
