// services/apiClient.js

// ── Mock users ─────────────────────────────────────────────────────────────────
var _mockUsers = JSON.parse(localStorage.getItem("pr_mock_users_v2") || JSON.stringify([
  {id:"u1", name:"Demo Customer",  email:"demo@playrent.com",  password:"demo123",  role:"CUSTOMER"},
  {id:"a1", name:"Admin",          email:"admin4451@gmail.com", password:"admin1155", role:"ADMIN"},
  {id:"s1", name:"Staff Member",   email:"staff4451@gmail.com", password:"staff1155", role:"STAFF"}
]));
var _mockRentals = JSON.parse(localStorage.getItem("pr_mock_rentals") || "[]");

function _saveUsers()   { localStorage.setItem("pr_mock_users_v2",   JSON.stringify(_mockUsers));   }
function _saveRentals() { localStorage.setItem("pr_mock_rentals", JSON.stringify(_mockRentals)); }

// ── Mock handler ───────────────────────────────────────────────────────────────
function _mock(path, opts) {
  var method = ((opts && opts.method) || "GET").toUpperCase();
  var body   = (opts && opts.body) ? JSON.parse(opts.body) : {};

  if (path === "/auth/login" && method === "POST") {
    var u = _mockUsers.filter(function(x){ return x.email===body.email && x.password===body.password; })[0];
    if (!u) throw new Error("Invalid email or password");
    return { token:"mock-"+u.id, user:{id:u.id,name:u.name,email:u.email,role:u.role} };
  }
  if (path === "/auth/register" && method === "POST") {
    if (_mockUsers.filter(function(x){ return x.email===body.email; }).length)
      throw new Error("Email already in use");
    var nu = {id:"u"+Date.now(), name:body.name, email:body.email, password:body.password, role:"CUSTOMER"};
    _mockUsers.push(nu); _saveUsers();
    return { token:"mock-"+nu.id, user:{id:nu.id,name:nu.name,email:nu.email,role:nu.role} };
  }
  if (path === "/equipment" && method === "GET") return MOCK_EQUIPMENT;
  if (path.indexOf("/equipment/") === 0 && method === "GET") {
    var eid = path.split("/").pop();
    return MOCK_EQUIPMENT.filter(function(e){ return e.id===eid; })[0] || null;
  }
  if (path.indexOf("/equipment/") === 0 && method === "PUT") {
    var eid = path.split("/")[2];
    var eq = MOCK_EQUIPMENT.filter(function(e){ return e.id===eid; })[0];
    if (eq) {
      if (body.price !== undefined) eq.price = Number(body.price);
      if (body.stock !== undefined) eq.stock = Number(body.stock);
      if (window.saveMockEquipment) window.saveMockEquipment();
    }
    return eq;
  }
  if (path === "/rentals" && method === "GET") return _mockRentals;
  if (path === "/rentals" && method === "POST") {
    var eq = MOCK_EQUIPMENT.filter(function(e){ return e.id===body.equipmentId; })[0];
    var r = {
      id: "r"+Date.now(),
      equipmentName:  eq ? eq.name  : "",
      equipmentEmoji: eq ? eq.emoji : "",
      status: "PENDING",
      createdAt: new Date().toISOString()
    };
    Object.keys(body).forEach(function(k){ r[k] = body[k]; });
    _mockRentals.unshift(r); _saveRentals();
    return r;
  }
  if (path.indexOf("/rentals/") === 0 && method === "PUT") {
    var rid = path.split("/")[2];
    var rr = _mockRentals.filter(function(x){ return x.id===rid; })[0];
    if (rr) { Object.keys(body).forEach(function(k){ rr[k]=body[k]; }); _saveRentals(); }
    return rr;
  }
  if (path === "/users" && method === "GET") {
    return _mockUsers.map(function(u){ return {id:u.id, name:u.name, email:u.email, role:u.role}; });
  }
  if (path.indexOf("/users/") === 0 && method === "DELETE") {
    var uid = path.split("/")[2];
    var beforeLen = _mockUsers.length;
    _mockUsers = _mockUsers.filter(function(x){ return x.id !== uid; });
    if (_mockUsers.length < beforeLen) {
      _saveUsers();
      _mockRentals = _mockRentals.filter(function(r){ return r.userId !== uid; });
      _saveRentals();
      return { success: true };
    }
    throw new Error("User not found");
  }
  return null;
}

// ── Core fetch ─────────────────────────────────────────────────────────────────
function _call(path, opts) {
  var headers = { "Content-Type":"application/json" };
  var tok = session.token();
  if (tok) headers["Authorization"] = "Bearer " + tok;

  return fetch(Config.API_BASE + path, Object.assign({}, opts, {headers:headers}))
    .then(function(res) {
      return res.text().then(function(txt) {
        var data = txt ? JSON.parse(txt) : null;
        if (!res.ok) throw new Error((data && data.error) || res.statusText);
        return data;
      });
    })
    .catch(function(e) {
      // Only fall back to mock on genuine network/connection errors (fetch failed entirely)
      if (Config.USE_MOCK_FALLBACK && (e instanceof TypeError || e.message === "Failed to fetch" || e.message.indexOf("NetworkError") >= 0 || e.message.indexOf("fetch") >= 0 && e.message.indexOf("network") >= 0)) {
        console.warn("[api] offline — using mock for:", path);
        try {
          return Promise.resolve(_mock(path, opts));
        } catch(mockErr) {
          return Promise.reject(mockErr);
        }
      }
      // Re-throw HTTP errors and mock errors as-is
      return Promise.reject(e);
    });
}

// ── Public API ─────────────────────────────────────────────────────────────────
var api = {
  login:         function(email,pw)      { return _call("/auth/login",    {method:"POST",body:JSON.stringify({email:email,password:pw})}); },
  register:      function(name,email,pw) { return _call("/auth/register", {method:"POST",body:JSON.stringify({name:name,email:email,password:pw})}); },
  listEquipment: function()              { return _call("/equipment"); },
  getEquipment:  function(id)            { return _call("/equipment/"+id); },
  updateEquipment: function(id,patch)    { return _call("/equipment/"+id, {method:"PUT", body:JSON.stringify(patch)}); },
  listRentals:   function()              { return _call("/rentals"); },
  createRental:  function(data)          { return _call("/rentals",      {method:"POST",body:JSON.stringify(data)}); },
  updateRental:  function(id,patch)      { return _call("/rentals/"+id,  {method:"PUT", body:JSON.stringify(patch)}); },
  listUsers:     function()              { return _call("/users"); },
  deleteUser:    function(id)            { return _call("/users/"+id,    {method:"DELETE"}); }
};
