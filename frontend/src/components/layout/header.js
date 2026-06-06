// components/layout/header.js — builds the shared nav bar

function buildNav(active, dark) {
  var u = session.user();
  var cls = "pr-nav" + (dark ? " dark" : "");

  var linksHtml = "";
  if (u && u.role === "ADMIN") {
    linksHtml += '<a href="' + ROOT + 'src/pages/admin/admin.html" class="' + (active==="admin"?"active":"") + '">Admin</a>';
  } else if (u && u.role === "STAFF") {
    linksHtml += '<a href="' + ROOT + 'src/pages/staff/staff.html" class="' + (active==="staff"?"active":"") + '">Staff</a>';
  } else {
    linksHtml += '<a href="' + ROOT + 'src/pages/catalog/catalog.html" class="' + (active==="catalog"?"active":"") + '">Catalog</a>';
    if (u) {
      linksHtml += '<a href="' + ROOT + 'src/pages/dashboard/dashboard.html" class="' + (active==="myrentals"?"active":"") + '">My Rentals</a>';
    }
  }

  var themeToggleBtn = '<button class="btn btn-outline" id="theme-toggle" onclick="toggleTheme()" style="padding: 8px; border-radius: 50%; width: 36px; height: 36px; display: grid; place-items: center; min-width: 36px; margin-right: 8px;" title="Switch Theme">' + getIcon('theme', 18) + '</button>';

  return '<nav class="' + cls + '" id="prNav">' +
    '<a href="' + ROOT + 'index.html" class="pr-logo">' +
      '<span class="pr-logo-mark">P</span> PlayRent' +
    '</a>' +
    '<div class="pr-nav-links">' +
      linksHtml +
    '</div>' +
    '<div class="pr-nav-right">' +
      themeToggleBtn +
      (u
        ? '<span style="font-size:13px;color:#6b7280">' + u.name + '</span>' +
          '<button class="btn btn-outline" onclick="session.clear();location.href=\'' + ROOT + 'index.html\'">Sign out</button>'
        : '<a href="' + ROOT + 'src/pages/auth/login.html"    class="btn btn-outline">Sign in</a>' +
          '<a href="' + ROOT + 'src/pages/catalog/catalog.html" class="btn btn-dark">Rent now</a>'
      ) +
    '</div>' +
  '</nav>';
}

function mountNav(active, dark) {
  document.body.insertAdjacentHTML("afterbegin", buildNav(active, dark));
}
