let restaurants = [];
let categories = [];
const state = { query: '', category: '', filter: 'all', sort: 'recommended', cart: {}, favorites: new Set() };
const $ = selector => document.querySelector(selector);
const money = value => `₹${Number(value).toLocaleString('en-IN')}`;
let currentUser = null;
let authMode = 'login';
let verifiedPhoneForOrder = '';
let trackerTimers = [];

function foodPhoto(name = '', imageUrl = '') {
  if (typeof imageUrl === 'string' && imageUrl.startsWith('/images/')) return imageUrl;
  const dish = name.toLowerCase();
  if (dish.includes('madurai specials')) return '/images/kari-dosa.jpg';
  if (dish.includes('drinks') || dish.includes('dessert')) return '/images/jigarthanda.jpg';
  if (dish.includes('non-veg') || dish.includes('non veg')) return '/images/user-nonveg-meals.png';
  if (dish.includes('south indian meals')) return '/images/south-indian-meals-user.png';
  if (dish.includes('south indian')) return '/images/idli.jpg';
  if (dish.includes('mutton kari dosa') || dish.includes('madurai kari dosa')) return '/images/mutton-kari-dosa-user.png';
  if (dish.includes('egg dosa')) return '/images/egg-dosa-user.png';
  if (dish.includes('mutton kola')) return '/images/mutton-kola-urundai-user.png';
  if (dish.includes('podi idli')) return '/images/podi-idli-user.png';
  if (dish.includes('egg kothu')) return '/images/egg-kothu-parotta-user.png';
  if (dish.includes('bun parotta')) return '/images/bun-parotta-user.png';
  if (dish.includes('curd rice')) return '/images/curd-rice-user.png';
  if (dish.includes('sweet coconut milk')) return '/images/sweet-coconut-milk-user.png';
  if (dish.includes('idiyappam') && dish.includes('curry')) return '/images/idiyappam-curry-user.png';
  if (dish.includes('idiyappam set')) return '/images/idiyappam-set-user.png';
  if (dish.includes('idiyappam') && dish.includes('coconut milk')) return '/images/idiyappam-coconut-milk-user.png';
  if (dish.includes('rava dosa') || dish.includes('rava dosai')) return '/images/rava-dosa-user.png';
  if (dish.includes('rose milk') || dish.includes('rosemilk')) return '/images/rosemilk-user.png';
  if (dish.includes('badam milk') || dish.includes('padam milk')) return '/images/badam-milk-user.png';
  if (dish.includes('kothu')) return '/images/user-kothu-parotta.png';
  if (dish.includes('parotta')) return '/images/user-parotta.png';
  if (dish.includes('dosa')) return '/images/kari-dosa.jpg';
  if (dish.includes('idiyappam')) return '/images/idiyappam.jpg';
  if (dish.includes('idli')) return '/images/idli.jpg';
  if (dish.includes('nannari')) return '/images/nannari-sarbath.jpg';
  if (dish.includes('jigarthanda')) return '/images/jigarthanda.jpg';
  if (dish.includes('coffee')) return '/images/filter-coffee.jpg';
  if (dish.includes('pongal')) return '/images/pongal.jpg';
  if (dish.includes('biryani')) return '/images/biryani.jpg';
  if (dish.includes('vadai') || dish.includes('vada')) return '/images/vadai.jpg';
  if (dish.includes('chukka')) return dish.includes('mutton') ? '/images/mutton-chukka.jpg' : '/images/chicken-chukka.jpg';
  if (dish.includes('brain')) return '/images/brain-roast.jpg';
  if (dish.includes('liver')) return '/images/mutton-liver.jpg';
  if (dish.includes('kola') || dish.includes('roast')) return '/images/mutton-kola.jpg';
  if (dish.includes('meal') || dish.includes('thali') || dish.includes('curd rice')) return '/images/thali.jpg';
  return '/images/idli.jpg';
}

function categoryPhotos(name = '') {
  const category = name.toLowerCase();
  if (category.includes('madurai specials')) return ['/images/user-kothu-parotta.png'];
  if (category.includes('south indian')) return ['/images/south-indian-meals-user.png'];
  if (category.includes('drinks')) return ['/images/jigarthanda.jpg'];
  if (category.includes('parotta')) return ['/images/user-parotta.png'];
  if (category.includes('non-veg') || category.includes('non veg')) {
    return ['/images/user-nonveg-meals.png'];
  }
  return [foodPhoto(name)];
}

function restaurantFoodName(restaurant) {
  if (restaurant.name.toLowerCase().includes('famous jigarthanda')) return 'Jigarthanda';
  return restaurantFoodItem(restaurant)?.name
    || '';
}

function restaurantFoodItem(restaurant) {
  if (restaurant.name.toLowerCase().includes('famous jigarthanda')) {
    return restaurant.menu.find(food => food.name.toLowerCase().includes('jigarthanda'))
      || restaurant.menu[0]
      || null;
  }
  return restaurant.menu.find(food => !/\b(tea|coffee|sarbath|jigarthanda)\b/i.test(food.name))
    || restaurant.menu[0]
    || null;
}

function renderCategories() {
  categories = [...new Set(restaurants.map(restaurant => {
    const name = restaurant.cuisine.split(' · ')[0];
    return name;
  }))];
  $('#categoryRow').innerHTML = categories.map(name =>
    `<button class="category-card ${state.category === name ? 'selected' : ''}" data-category="${name}"><span class="category-photo-set ${categoryPhotos(name).length > 1 ? 'multi' : ''}">${categoryPhotos(name).map(src => `<img src="${src}" alt="">`).join('')}</span><span>${name}</span></button>`
  ).join('');
}

function matchingHotels(query) {
  const value = query.trim().toLowerCase();
  if (!value) return [];
  return restaurants.filter(restaurant =>
    restaurant.name.toLowerCase().includes(value)
    || restaurant.menu.some(food => food.name.toLowerCase().includes(value))
    || restaurant.area?.toLowerCase().includes(value)
  ).slice(0, 5);
}

function renderSuggestions() {
  const box = $('#searchSuggestions');
  const matches = matchingHotels($('#searchInput').value);
  box.innerHTML = matches.map(restaurant => `
    <button class="suggestion-row" type="button" data-search-restaurant="${restaurant.id}">
      <img class="suggestion-photo" src="${foodPhoto(restaurantFoodName(restaurant), restaurantFoodItem(restaurant)?.imageUrl)}" alt="">
      <span class="suggestion-copy"><b>${restaurant.name}</b><small>${restaurant.area || 'Madurai'} · ${restaurant.menu.length} dishes</small></span>
      <span class="suggestion-arrow">→</span>
    </button>`).join('');
  box.hidden = matches.length === 0;
}

function visibleRestaurants() {
  const query = state.query.toLowerCase();
  let result = restaurants.filter(restaurant => {
    const searchable = `${restaurant.name} ${restaurant.cuisine} ${restaurant.description} ${restaurant.menu.map(item => item.name).join(' ')}`.toLowerCase();
    const searchMatch = !query || searchable.includes(query);
    const categoryMatch = !state.category || `${restaurant.name} ${restaurant.cuisine} ${restaurant.description}`.toLowerCase().includes(state.category.toLowerCase());
    const filterMatch = state.filter === 'all'
      || (state.filter === 'rating' && restaurant.rating >= 4.5)
      || (state.filter === 'veg' && restaurant.veg)
      || (state.filter === 'fast' && restaurant.time < 30);
    return searchMatch && categoryMatch && filterMatch;
  });
  if (state.sort === 'rating') result.sort((a, b) => b.rating - a.rating);
  if (state.sort === 'delivery') result.sort((a, b) => a.time - b.time);
  return result;
}

function renderRestaurants() {
  const list = visibleRestaurants();
  $('#restaurantGrid').innerHTML = list.map(restaurant => `
    <article class="restaurant-card" data-restaurant="${restaurant.id}">
      <div class="restaurant-image" style="--restaurant-tone:${restaurant.tone}">
        <img class="food-photo" src="${foodPhoto(restaurantFoodName(restaurant), restaurantFoodItem(restaurant)?.imageUrl)}" alt="${restaurantFoodName(restaurant) || restaurant.name}">
        <span class="offer-label">${restaurant.offer}</span>
        <button class="heart-button ${state.favorites.has(restaurant.id) ? 'liked' : ''}" data-favorite="${restaurant.id}" aria-label="${state.favorites.has(restaurant.id) ? 'Remove from' : 'Add to'} favourites">${state.favorites.has(restaurant.id) ? '♥' : '♡'}</button>
      </div>
      <div class="restaurant-info">
        <div class="restaurant-title-row"><h3>${restaurant.name}</h3><span class="rating">★ ${restaurant.rating}</span></div>
        <div class="restaurant-meta"><span>${restaurant.cuisine.split(' · ')[0]}</span><i></i><span>${restaurant.menu.length} dishes</span></div>
        <div class="restaurant-description">${restaurant.description}</div>
        <div class="card-bottom"><span class="delivery-time">⌖ ${restaurant.area || 'Madurai'} · from ${money(restaurant.menu[0]?.price || 0)}</span><button class="menu-link" data-add="${restaurant.id}">View menu +</button></div>
      </div>
    </article>`).join('');
  $('#emptyState').hidden = list.length > 0;
}

function cartEntries() {
  return Object.entries(state.cart).filter(([, quantity]) => quantity > 0).map(([foodId, quantity]) => {
    const restaurant = restaurants.find(entry => entry.menu.some(food => food.id === Number(foodId)));
    const food = restaurant?.menu.find(entry => entry.id === Number(foodId));
    return { restaurant, food, quantity };
  }).filter(entry => entry.restaurant && entry.food);
}

function renderCart() {
  const items = cartEntries();
  const count = items.reduce((sum, entry) => sum + entry.quantity, 0);
  $('#cartCount').textContent = count;
  $('#drawerCount').textContent = `(${count})`;
  $('#cartFooter').hidden = count === 0;
  if (!count) {
    $('#cartContent').innerHTML = '<div class="empty-cart"><span>🛍️</span><h3>Your cart is empty</h3><p>Pick a Madurai favourite to fill it up.</p></div>';
    return;
  }
  $('#cartContent').innerHTML = items.map(({ restaurant, food, quantity }) => `
    <div class="cart-line"><img class="line-photo" src="${foodPhoto(food.name, food.imageUrl)}" alt="${food.name}"><div class="line-details"><b>${food.name}</b><small>${restaurant.name} · ${money(food.price)} each</small></div>
    <div class="quantity"><button data-qty="${food.id}" data-delta="-1" aria-label="Remove one">−</button><span>${quantity}</span><button data-qty="${food.id}" data-delta="1" aria-label="Add one">+</button></div></div>`).join('');
  const subtotal = items.reduce((sum, entry) => sum + entry.food.price * entry.quantity, 0);
  const delivery = subtotal >= 399 ? 0 : 25;
  $('#cartFooter').innerHTML = `<div class="bill-row"><span>Item total</span><span>${money(subtotal)}</span></div><div class="bill-row"><span>Delivery fee</span><span>${delivery ? money(delivery) : '<span style="color:#368461">FREE</span>'}</span></div><div class="bill-row total"><span>To pay</span><span>${money(subtotal + delivery)}</span></div><button class="checkout-button" id="checkoutButton">Place your order · ${money(subtotal + delivery)} <span>→</span></button>`;
}

function addItem(foodId, quantity = 1) {
  state.cart[foodId] = (state.cart[foodId] || 0) + quantity;
  if (state.cart[foodId] <= 0) delete state.cart[foodId];
  renderCart();
  const entry = cartEntries().find(item => item.food.id === foodId);
  showToast(entry ? `${entry.food.name} added to your cart` : 'Item removed from your cart');
}

let toastTimer;
function showToast(message) {
  const toast = $('#toast');
  toast.textContent = message;
  toast.classList.add('show');
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.classList.remove('show'), 2400);
}

function showModal(title, text, action = 'Sounds good') {
  $('#modalTitle').textContent = title;
  $('#modalText').textContent = text;
  $('#modalText').hidden = false;
  $('#authForm').hidden = true;
  $('#checkoutForm').hidden = true;
  $('#orderTracker').hidden = true;
  $('#modalAction').hidden = false;
  $('#modalAction').dataset.action = 'close';
  $('#menuList').hidden = true;
  $('#menuList').innerHTML = '';
  $('#modalAction').textContent = action;
  $('#infoModal').showModal();
}

function openCheckoutDialog() {
  if (!cartEntries().length) return;
  showModal('Where should we deliver?', 'Add your contact details so the restaurant can prepare your order.', '');
  $('#modalAction').hidden = true;
  $('#checkoutForm').hidden = false;
  $('#deliveryName').value = currentUser?.name || '';
  $('#deliveryPhone').value = '';
  $('#deliveryAddress').value = '';
  $('#deliveryOtp').value = '';
  $('#otpCodeRow').hidden = true;
  $('#otpStatus').textContent = 'Enter your number to get a verification code.';
  $('#otpStatus').className = 'otp-status';
  $('#otpCodeDisplay').hidden = true;
  $('#otpCodeValue').textContent = '';
  $('#checkoutSubmit').disabled = true;
  $('#checkoutSubmit').textContent = 'Verify phone to continue';
  verifiedPhoneForOrder = '';
  $('#checkoutMessage').textContent = '';
  $('#checkoutMessage').classList.remove('success');
}

function updateTrackerStep(step, title, subtitle) {
  $('#trackerStatus').textContent = title;
  $('#trackerSubtitle').textContent = subtitle;
  document.querySelectorAll('.tracker-progress span').forEach((node, index) => {
    node.classList.toggle('active', index <= step);
  });
  $('#trackerScooter').style.left = step < 2 ? '6%' : step === 2 ? '54%' : '92%';
  $('#trackerScooter').textContent = step === 3 ? '✓' : '🛵';
}

function startOrderTracker(orderId, orderedEntries, address, deliveryMinutes) {
  trackerTimers.forEach(clearTimeout);
  trackerTimers = [];
  const restaurantNames = [...new Set(orderedEntries.map(entry => entry.restaurant.name))];
  $('#trackerRestaurant').textContent = restaurantNames.length === 1
    ? restaurantNames[0]
    : `${restaurantNames.length} restaurants`;
  $('#trackerAddress').textContent = address;
  $('#trackerAddress').title = address;
  $('#modalTitle').textContent = 'Order placed successfully!';
  $('#modalText').textContent = `Your food is on the way! Order #${orderId} · estimated delivery ${deliveryMinutes} minutes.`;
  $('#orderTracker').hidden = false;
  $('#modalAction').textContent = 'Continue browsing';
  updateTrackerStep(0, 'Order confirmed', 'The restaurant is preparing your food.');
  trackerTimers.push(setTimeout(() => updateTrackerStep(2, 'Your food is on the way!', 'The delivery is moving towards your address.'), 5000));
  trackerTimers.push(setTimeout(() => updateTrackerStep(3, 'Delivered — enjoy your meal!', 'Your Madurai favourite has reached your door.'), 18000));
}

function updateProfileButton() {
  $('#profileButton').querySelector('span:last-child').textContent = currentUser?.name || 'Sign in';
}

async function refreshCurrentUser() {
  try {
    const response = await fetch('/auth/me');
    if (response.ok) currentUser = await response.json();
    updateProfileButton();
  } catch (_) { /* Sign-in remains available when no session is active. */ }
}

function setAuthMode(mode) {
  authMode = mode;
  const registering = mode === 'register';
  $('#modalTitle').textContent = registering ? 'Create your Crave account' : 'Sign in to Crave';
  $('#modalText').textContent = registering ? 'Save your favourites and place orders.' : 'Welcome back. Sign in to continue.';
  $('#authNameRow').hidden = !registering;
  $('#authName').required = registering;
  $('#authPassword').autocomplete = registering ? 'new-password' : 'current-password';
  $('#authSubmit').textContent = registering ? 'Create account' : 'Sign in';
  $('#authModeToggle').textContent = registering ? 'Already have an account? Sign in' : 'New to Crave? Create an account';
  $('#authMessage').textContent = '';
  $('#authMessage').classList.remove('success');
}

function openAuthDialog() {
  if (currentUser) {
    showModal(`Welcome, ${currentUser.name}`, currentUser.email, 'Sign out');
    $('#modalAction').dataset.action = 'logout';
    return;
  }
  $('#menuList').hidden = true;
  $('#checkoutForm').hidden = true;
  $('#orderTracker').hidden = true;
  $('#modalText').hidden = false;
  $('#modalAction').hidden = true;
  $('#modalAction').dataset.action = 'close';
  $('#authForm').hidden = false;
  setAuthMode('login');
  $('#infoModal').showModal();
}

async function responseData(response) {
  const data = await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(data.detail || data.message || 'Could not sign in. Please try again.');
  return data;
}

function openMenu(restaurant) {
  if (!restaurant) return;
  showModal(restaurant.name, `${restaurant.cuisine} · ${restaurant.time} min delivery estimate`, 'Close menu');
  $('#menuList').innerHTML = restaurant.menu.map(food => `
    <article class="menu-item"><div class="menu-item-photo"><img src="${foodPhoto(food.name, food.imageUrl)}" alt="${food.name}"></div><div class="menu-item-details"><span>${food.name}<small>${money(food.price)} · freshly prepared</small></span><button data-menu-add="${food.id}">ADD +</button></div></article>`).join('');
  $('#menuList').hidden = false;
}

function openCart() {
  $('#overlay').hidden = false;
  requestAnimationFrame(() => $('#overlay').classList.add('visible'));
  $('#cartDrawer').classList.add('open');
  $('#cartDrawer').setAttribute('aria-hidden', 'false');
  document.body.style.overflow = 'hidden';
}

function closeCart() {
  $('#overlay').classList.remove('visible');
  $('#cartDrawer').classList.remove('open');
  $('#cartDrawer').setAttribute('aria-hidden', 'true');
  document.body.style.overflow = '';
  setTimeout(() => $('#overlay').hidden = true, 250);
}

renderCategories();
renderRestaurants();
renderCart();

async function loadRestaurants() {
  try {
    const response = await fetch('/api/restaurants');
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    restaurants = await response.json();
    renderCategories();
    renderRestaurants();
    renderCart();
  } catch (error) {
    showToast('Java backend-ஐ அடைய முடியவில்லை. Spring Boot-ல் திறக்கவும்.');
  }
}
loadRestaurants();
refreshCurrentUser();

$('#searchInput').addEventListener('input', event => {
  state.query = event.target.value;
  if (state.query.trim()) {
    state.category = '';
    state.filter = 'all';
    document.querySelectorAll('.filter-chip').forEach(chip => chip.classList.toggle('selected', chip.dataset.filter === 'all'));
    renderCategories();
  }
  renderSuggestions();
  renderRestaurants();
});
$('#searchInput').addEventListener('focus', renderSuggestions);
$('#searchInput').addEventListener('keydown', event => { if (event.key === 'Enter') { const hotel = matchingHotels(event.target.value)[0]; if (hotel) { event.preventDefault(); $('#searchSuggestions').hidden = true; openMenu(hotel); } } });
$('#searchSuggestions').addEventListener('click', event => { const button = event.target.closest('[data-search-restaurant]'); if (!button) return; const hotel = restaurants.find(restaurant => restaurant.id === Number(button.dataset.searchRestaurant)); $('#searchSuggestions').hidden = true; openMenu(hotel); });
document.addEventListener('click', event => { if (!event.target.closest('.search-wrap')) $('#searchSuggestions').hidden = true; });
$('#searchFocus').addEventListener('click', () => { document.querySelector('.hero').scrollIntoView({ behavior: 'smooth' }); setTimeout(() => $('#searchInput').focus(), 350); });
document.addEventListener('keydown', event => { if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === 'k') { event.preventDefault(); $('#searchInput').focus(); } if (event.key === 'Escape') closeCart(); });

$('#categoryRow').addEventListener('click', event => {
  const button = event.target.closest('[data-category]');
  if (!button) return;
  state.category = state.category === button.dataset.category ? '' : button.dataset.category;
  state.query = '';
  $('#searchInput').value = '';
  state.filter = 'all';
  document.querySelectorAll('.filter-chip').forEach(chip => chip.classList.toggle('selected', chip.dataset.filter === 'all'));
  $('#searchSuggestions').hidden = true;
  renderCategories(); renderRestaurants();
  $('#restaurants').scrollIntoView({ behavior: 'smooth', block: 'start' });
});
$('#clearCategory').addEventListener('click', () => { state.category = ''; state.query = ''; $('#searchInput').value = ''; state.filter = 'all'; document.querySelectorAll('.filter-chip').forEach(button => button.classList.toggle('selected', button.dataset.filter === 'all')); renderCategories(); renderRestaurants(); });
$('#filterRow').addEventListener('click', event => { const button = event.target.closest('[data-filter]'); if (!button) return; state.filter = button.dataset.filter; document.querySelectorAll('.filter-chip').forEach(chip => chip.classList.toggle('selected', chip === button)); renderRestaurants(); });
$('#sortSelect').addEventListener('change', event => { state.sort = event.target.value; renderRestaurants(); });
$('#restaurantGrid').addEventListener('click', event => {
  const favorite = event.target.closest('[data-favorite]');
  if (favorite) { const id = Number(favorite.dataset.favorite); state.favorites.has(id) ? state.favorites.delete(id) : state.favorites.add(id); renderRestaurants(); return; }
  const card = event.target.closest('[data-restaurant]');
  if (card) openMenu(restaurants.find(entry => entry.id === Number(card.dataset.restaurant)));
});
$('#menuList').addEventListener('click', event => { const button = event.target.closest('[data-menu-add]'); if (!button) return; addItem(Number(button.dataset.menuAdd)); });
$('#resetSearch').addEventListener('click', () => { $('#searchInput').value = ''; state.query = ''; state.filter = 'all'; state.category = ''; document.querySelectorAll('.filter-chip').forEach(button => button.classList.toggle('selected', button.dataset.filter === 'all')); renderCategories(); renderRestaurants(); });

$('#cartButton').addEventListener('click', openCart);
$('#closeCart').addEventListener('click', closeCart);
$('#overlay').addEventListener('click', closeCart);
$('#cartContent').addEventListener('click', event => { const button = event.target.closest('[data-qty]'); if (button) addItem(Number(button.dataset.qty), Number(button.dataset.delta)); });
$('#cartFooter').addEventListener('click', event => {
  if (event.target.closest('#checkoutButton')) openCheckoutDialog();
});

$('#checkoutForm').addEventListener('submit', async event => {
  event.preventDefault();
  const button = $('#checkoutSubmit');
  const message = $('#checkoutMessage');
  const orderedEntries = cartEntries();
  const address = $('#deliveryAddress').value.trim();
  const deliveryMinutes = Math.max(...orderedEntries.map(entry => entry.restaurant.time));
  button.disabled = true;
  message.textContent = '';
  try {
    const response = await fetch('/api/orders', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        items: orderedEntries.map(({ food, quantity }) => ({ foodId: food.id, quantity })),
        customerName: $('#deliveryName').value.trim(),
        phone: $('#deliveryPhone').value.trim(),
        address: $('#deliveryAddress').value.trim()
      })
    });
    const receipt = await responseData(response);
    state.cart = {};
    renderCart();
    $('#infoModal').close();
    closeCart();
    showModal('Order placed successfully!', '', 'Continue browsing');
    startOrderTracker(receipt.orderId, orderedEntries, address, deliveryMinutes);
  } catch (error) {
    message.textContent = error.message.includes('Failed to fetch')
      ? 'Could not reach the Java backend. Start Spring Boot and try again.'
      : error.message;
  } finally {
    button.disabled = false;
  }
});

$('#deliveryPhone').addEventListener('input', () => {
  if ($('#deliveryPhone').value.trim() === verifiedPhoneForOrder) return;
  verifiedPhoneForOrder = '';
  $('#checkoutSubmit').disabled = true;
  $('#checkoutSubmit').textContent = 'Verify phone to continue';
  $('#deliveryOtp').value = '';
  $('#otpCodeRow').hidden = true;
  $('#otpCodeDisplay').hidden = true;
  $('#otpCodeValue').textContent = '';
  $('#otpStatus').textContent = 'Phone changed. Send and verify a new code.';
  $('#otpStatus').className = 'otp-status';
});

$('#sendOtpButton').addEventListener('click', async () => {
  const phone = $('#deliveryPhone').value.trim();
  const button = $('#sendOtpButton');
  const status = $('#otpStatus');
  if (!/^[0-9+() -]{10,16}$/.test(phone) || phone.replace(/\D/g, '').replace(/^91(?=\d{10}$)/, '').length !== 10) {
    status.textContent = 'Enter a valid 10-digit Indian mobile number first.';
    status.className = 'otp-status error';
    $('#deliveryPhone').focus();
    return;
  }
  button.disabled = true;
  button.textContent = 'Sending…';
  status.className = 'otp-status';
  try {
    const response = await fetch('/api/orders/otp/send', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ phone })
    });
    const result = await responseData(response);
    $('#otpCodeRow').hidden = false;
    $('#otpCodeDisplay').hidden = false;
    $('#otpCodeValue').textContent = result.verificationCode;
    $('#deliveryOtp').value = '';
    $('#deliveryOtp').focus();
    status.textContent = `${result.message} Sent to ${result.maskedPhone}. Expires in 5 minutes.`;
    status.className = 'otp-status';
  } catch (error) {
    status.textContent = error.message.includes('Failed to fetch')
      ? 'Could not reach the Java backend. Start Spring Boot and try again.'
      : error.message;
    status.className = 'otp-status error';
  } finally {
    button.disabled = false;
  button.textContent = 'Send OTP';
  }
});

$('#verifyOtpButton').addEventListener('click', async () => {
  const phone = $('#deliveryPhone').value.trim();
  const code = $('#deliveryOtp').value.trim();
  const status = $('#otpStatus');
  const button = $('#verifyOtpButton');
  if (!/^[0-9]{6}$/.test(code)) {
    status.textContent = 'Enter the 6-digit verification code shown above.';
    status.className = 'otp-status error';
    $('#deliveryOtp').focus();
    return;
  }
  button.disabled = true;
  button.textContent = 'Checking…';
  try {
    const response = await fetch('/api/orders/otp/verify', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ phone, code })
    });
    const result = await responseData(response);
    verifiedPhoneForOrder = phone;
    status.textContent = `${result.message} You can confirm the order now.`;
    status.className = 'otp-status success';
    $('#checkoutSubmit').disabled = false;
    $('#checkoutSubmit').textContent = 'Confirm order';
  } catch (error) {
    status.textContent = error.message.includes('Failed to fetch')
      ? 'Could not reach the Java backend. Start Spring Boot and try again.'
      : error.message;
    status.className = 'otp-status error';
  } finally {
    button.disabled = false;
    button.textContent = 'Verify';
  }
});

$('#profileButton').addEventListener('click', openAuthDialog);
$('#locationButton').addEventListener('click', () => showModal('Delivering around Madurai', 'Explore favourite dishes from Madurai restaurants and local kitchens.'));
function navigatePage(page) {
  const target = document.getElementById(page);
  if (!target) return;
  document.querySelectorAll('.nav-links [data-page]').forEach(link => link.classList.toggle('active', link.dataset.page === page));
  target.scrollIntoView({ behavior: 'smooth', block: 'start' });
  history.replaceState(null, '', `#${page}`);
}
document.querySelectorAll('[data-page]').forEach(link => link.addEventListener('click', event => {
  const page = event.currentTarget.dataset.page;
  if (!page) return;
  event.preventDefault();
  navigatePage(page);
}));
$('#offerButton').addEventListener('click', () => navigatePage('restaurants'));
$('#contactForm').addEventListener('submit', event => {
  event.preventDefault();
  const name = $('#contactName').value.trim();
  $('#contactMessage').textContent = `Thanks, ${name}! Your message is on its way to the Crave team.`;
  $('#contactMessage').classList.add('success');
  $('#contactForm').reset();
});
$('#closeModal').addEventListener('click', () => $('#infoModal').close());
$('#modalAction').addEventListener('click', async () => {
  if ($('#modalAction').dataset.action === 'logout') {
    try { await fetch('/auth/logout', { method: 'POST' }); } catch (_) { /* The local session may already be closed. */ }
    currentUser = null;
    updateProfileButton();
  }
  $('#infoModal').close();
});
$('#authModeToggle').addEventListener('click', () => setAuthMode(authMode === 'login' ? 'register' : 'login'));
$('#authForm').addEventListener('submit', async event => {
  event.preventDefault();
  const submit = $('#authSubmit');
  const message = $('#authMessage');
  const name = $('#authName').value.trim();
  const email = $('#authEmail').value.trim().toLowerCase();
  const password = $('#authPassword').value;
  submit.disabled = true;
  message.textContent = '';
  try {
    if (authMode === 'register') {
      const registration = await fetch('/auth/register', {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password })
      });
      await responseData(registration);
    }
    const login = await fetch('/auth/login', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    currentUser = await responseData(login);
    updateProfileButton();
    $('#infoModal').close();
    showToast(`Welcome, ${currentUser.name}`);
    $('#authForm').reset();
  } catch (error) {
    message.textContent = error.message.includes('Failed to fetch')
      ? 'Could not reach the Java backend. Start Spring Boot and try again.'
      : error.message;
  } finally {
    submit.disabled = false;
  }
});
$('#infoModal').addEventListener('click', event => { if (event.target === $('#infoModal')) $('#infoModal').close(); });
