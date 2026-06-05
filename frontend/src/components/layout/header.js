// components/layout/header.js — builds the shared nav bar

function buildNav(active, dark) {
  var u = session.user();
  var cls = "pr-nav" + (dark ? " dark" : "");
  return '<nav class="' + cls + '" id="prNav">' +
    '<a href="' + ROOT + 'index.html" class="pr-logo">' +
      '<span class="pr-logo-mark">P</span> PlayRent' +
    '</a>' +
    '<div class="pr-nav-links">' +
      '<a href="' + ROOT + 'src/pages/catalog/catalog.html" class="' + (active==="catalog"?"active":"") + '">Catalog</a>' +
      (u ? '<a href="' + ROOT + 'src/pages/dashboard/dashboard.html" class="' + (active==="myrentals"?"active":"") + '">My Rentals</a>' : '') +
      (u && u.role==="STAFF" ? '<a href="' + ROOT + 'src/pages/staff/staff.html" class="' + (active==="staff"?"active":"") + '">Staff</a>' : '') +
      (u && u.role==="ADMIN" ? '<a href="' + ROOT + 'src/pages/admin/admin.html" class="' + (active==="admin"?"active":"") + '">Admin</a>' : '') +
    '</div>' +
    '<div class="pr-nav-right">' +
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
