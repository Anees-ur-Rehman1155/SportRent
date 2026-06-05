// utils/helpers.js

function todayString() { return new Date().toISOString().split("T")[0]; }

function statusClass(s) {
  if (s === "APPROVED") return "badge-green";
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
