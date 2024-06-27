
/* Service */
// Set Cookie Function
function setCookie(name, value, days) {
  var expires = "";
  if (days) {
    var date = new Date();
    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
    expires = "; expires=" + date.toUTCString();
  }
  document.cookie = name + "=" + (value || "") + expires + "; path=/";
}
// Get Cookie Function
function getCookie(name) {
  var nameKI = name + "=";
  var ca = document.cookie.split(';');
  for (var i = 0; i < ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0) == ' ') c = c.substring(1, c.length);
    if (c.indexOf(nameKI) == 0) return c.substring(nameKI.length, c.length);
  }
  return null;
}

var PopupKey = 'app-popup';
var PopupValue = PopupKey;
const installButton = document.getElementById('install_btn');
if ('serviceWorker' in navigator && 'PushManager' in window) {
  navigator.serviceWorker.register('/js/service-worker.js')
    .then((registration) => {
      console.log('registered with scope:', registration.scope);
    })
    .catch((error) => {
      console.error('Service Worker registration failed:', error);
    });
}

var deferredPrompt;
window.addEventListener('beforeinstallprompt', function (e) {
  e.preventDefault();
  deferredPrompt = e;
  return false;
});

// Button click
installButton.addEventListener('click', function () {
  if (deferredPrompt !== undefined) {

    deferredPrompt.prompt();

    deferredPrompt.userChoice.then(function (choiceResult) {
      deferredPrompt = null;
    });
  }
});

window.addEventListener('appinstalled', (event) => {
  localStorage.setItem("appinstalled", true);
});

// Window load popup open
$(window).on("load", async function () {
  response = localStorage.getItem('appinstalled');
  if (response) return;
  setTimeout(() => {
    $(".app-popup").slideDown("fast");
    $(".popup-overlay").fadeIn("fast");
  }, "500");
});

// Onclick Popup hide
$('.popup-overlay, .close-popup, #install_btn').on('click', function () {
  setTimeout(() => {
    $(".app-popup").slideUp("fast");
    $(".popup-overlay").fadeOut("fast");
  }, "500");
});





async function addToCache(urlsToCache) {
  const cacheName = 'guimasbet-app-cache';

  try {
      const cache = await caches.open(cacheName);
      await cache.addAll(urlsToCache);
      console.log('Resources were added to cache');
  } catch (error) {
      console.error('Failed to add resources to cache', error);
  }
}

async function fetchFromCache(requestUrl) {
  try {
      const cacheResponse = await caches.match(requestUrl);
      if (cacheResponse) {
          
          return true;
      } else {
          return false;
      }
  } catch (error) {
      console.error('Error fetching resource', requestUrl, error);
      throw error;
  }
}