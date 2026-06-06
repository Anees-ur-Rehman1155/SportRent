// utils/helpers.js

// ── Theme Management ──────────────────────────────────────────────────────────
(function() {
  var mode = localStorage.getItem("pr_theme") || "light";
  document.documentElement.setAttribute("data-theme", mode);
})();

function toggleTheme() {
  var current = localStorage.getItem("pr_theme") || "light";
  var next = current === "dark" ? "light" : "dark";
  localStorage.setItem("pr_theme", next);
  document.documentElement.setAttribute("data-theme", next);
  window.dispatchEvent(new Event("themechanged"));
}
window.toggleTheme = toggleTheme;

// ── Date and Stock Helpers ───────────────────────────────────────────────────
function todayString() { return new Date().toISOString().split("T")[0]; }

function statusClass(s) {
  if (s === "APPROVED" || s === "RETURNED") return "badge-green";
  if (s === "REJECTED") return "badge-red";
  return "badge-yellow";
}

function stockClass(n) {
  if (n < 3) return "badge-red";
  if (n < 6) return "badge-yellow";
  return "badge-green";
}

function stockLabel(n) { return n > 0 ? n + " in stock" : "Sold out"; }

function toast(msg, type) {
  type = type || "success";
  var el = document.createElement("div");
  el.className = "pr-toast pr-toast-" + type;
  el.textContent = msg;
  document.body.appendChild(el);
  requestAnimationFrame(function() { el.classList.add("show"); });
  setTimeout(function() {
    el.classList.remove("show");
    setTimeout(function() { el.remove(); }, 400);
  }, 2800);
}

// ── SVG Vector Icon Repository ──────────────────────────────────────────────
function getIcon(key, size) {
  size = size || 24;
  var svgProps = 'width="' + size + '" height="' + size + '" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="svg-icon"';
  
  switch(key) {
    case 'football':
      return '<svg ' + svgProps + '><circle cx="12" cy="12" r="10"/><path d="M12 2v20M2 12h20M12 12l7.07-7.07M4.93 19.07l14.14-14.14M4.93 4.93l14.14 14.14"/></svg>';
    case 'basketball':
      return '<svg ' + svgProps + '><circle cx="12" cy="12" r="10"/><path d="M6 12A6 6 0 0 1 18 12M12 6A6 6 0 0 1 12 18M12 2v20M2 12h20"/></svg>';
    case 'cricket':
    case 'bat':
      return '<svg ' + svgProps + '><path d="M18.5 5.5a2.12 2.12 0 0 0-3 0L3 18v3h3L18.5 8.5a2.12 2.12 0 0 0 0-3zM15 9l3 3"/></svg>';
    case 'tennis':
    case 'racket':
      return '<svg ' + svgProps + '><circle cx="10" cy="10" r="7"/><path d="M15 15l6 6M10 10l3 3M7 7l3 3M11 6a3 3 0 0 1 3 3"/></svg>';
    case 'hockey':
    case 'hockey-stick':
      return '<svg ' + svgProps + '><path d="M6 3v13a4 4 0 0 0 4 4h8a2 2 0 0 0 2-2v-1a2 2 0 0 0-2-2h-6V3H6z"/></svg>';
    case 'volleyball':
      return '<svg ' + svgProps + '><circle cx="12" cy="12" r="10"/><path d="M12 2a10 10 0 0 0-7.5 16.6M12 22a10 10 0 0 0 7.5-16.6M2 12h20M12 2v20"/></svg>';
    case 'boots':
      return '<svg ' + svgProps + '><path d="M4 16v-2a4 4 0 0 1 4-4h4l6-3v6l2 1v5a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2zM8 10V6"/></svg>';
    case 'hoop':
      return '<svg ' + svgProps + '><path d="M3 3h18v4H3zM12 7v14M6 7v6a6 6 0 0 0 12 0V7"/></svg>';
    case 'pads':
      return '<svg ' + svgProps + '><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>';
    case 'tennis-ball':
      return '<svg ' + svgProps + '><circle cx="12" cy="12" r="10"/><path d="M6 12c0-3.3 2.7-6 6-6M12 18c3.3 0 6-2.7 6-6"/></svg>';
    case 'helmet':
      return '<svg ' + svgProps + '><path d="M2 11a10 10 0 0 1 20 0v3H2v-3zM5 14v4a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-4M9 14h6"/></svg>';
    case 'net':
      return '<svg ' + svgProps + '><rect x="2" y="6" width="20" height="12" rx="2"/><path d="M6 6v12M10 6v12M14 6v12M18 6v12M2 10h20M2 14h20"/></svg>';
    case 'analytics':
      return '<svg ' + svgProps + '><path d="M3 3v18h18M18.7 8l-5.1 5.2-2.8-2.7L7 14.3"/></svg>';
    case 'inventory':
    case 'package':
      return '<svg ' + svgProps + '><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16zM3.27 6.96L12 12.01l8.73-5.05M12 22.08V12"/></svg>';
    case 'rentals':
    case 'clipboard':
      return '<svg ' + svgProps + '><rect x="8" y="2" width="8" height="4" rx="1" fill="currentColor"/><path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2M9 14h6M9 18h4"/></svg>';
    case 'myrentals':
    case 'dashboard':
      return '<svg ' + svgProps + '><rect x="3" y="3" width="7" height="9" rx="1"/><rect x="14" y="3" width="7" height="5" rx="1"/><rect x="14" y="12" width="7" height="9" rx="1"/><rect x="3" y="16" width="7" height="5" rx="1"/></svg>';
    case 'browse':
    case 'cart':
      return '<svg ' + svgProps + '><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>';
    case 'signout':
      return '<svg ' + svgProps + '><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9"/></svg>';
    case 'theme':
      return '<svg ' + svgProps + '><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41M12 7a5 5 0 1 0 0 10z"/></svg>';
    case 'search':
      return '<svg ' + svgProps + '><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>';
    case 'star':
      return '<svg ' + svgProps + ' style="fill:currentColor;color:#fbbf24"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>';
    case 'check':
      return '<svg ' + svgProps + ' style="color:#22c55e"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>';
    default:
      return '<svg ' + svgProps + '><circle cx="12" cy="12" r="10"/></svg>';
  }
}
window.getIcon = getIcon;
