
/*----------------------------- Site Loader & Popup --------------------*/
self.addEventListener('install', (event) => {
	event.waitUntil(
		caches.open('guimasbet-app-cache').then((cache) => {
			return cache.addAll([
				'/',
				'/login',
				'/js/manifest.json',
				'/img/icons/icon-96x96.png',
				'/vendor/jquery/jquery.min.js',
				'/vendor/bootstrap/js/bootstrap.min.js',
				'/vendor/metisMenu/metisMenu.min.js',
				'/vendor/raphael/raphael.min.js',
				'/vendor/morrisjs/morris.min.js',
				'/dist/js/sb-admin-2.js',
				'/js/eruda.js',
				'/external/trumbowyg/dist/trumbowyg.min.js',
				'/js/google-client.js',
				'/vendor/bootstrap/css/bootstrap.min.css',
				'/vendor/metisMenu/metisMenu.min.css',
				'/dist/css/sb-admin-2.css',
				'/vendor/morrisjs/morris.css',
				'/bolao/css/custom.css',
				'/external/trumbowyg/dist/ui/trumbowyg.min.css'
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
  
  