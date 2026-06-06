// services/authService.js

var session = {
  user: function() {
    try { return JSON.parse(localStorage.getItem("pr_user")); } catch(e) { return null; }
  },
  token: function() { return localStorage.getItem("pr_token"); },
  set: function(token, user) {
    localStorage.setItem("pr_token", token);
    localStorage.setItem("pr_user", JSON.stringify(user));
  },
  clear: function() {
    localStorage.removeItem("pr_token");
    localStorage.removeItem("pr_user");
  },
  require: function(role) {
    var u = session.user();
    if (!u) { location.href = ROOT + "src/pages/auth/login.html"; return null; }
    if (role && u.role !== role && u.role !== "ADMIN") {
      toast("Access denied", "error");
      setTimeout(function() { location.href = ROOT + "index.html"; }, 800);
      return null;
    }
    return u;
  }
};
