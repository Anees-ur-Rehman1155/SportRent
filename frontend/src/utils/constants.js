// utils/constants.js

var SPORTS_LIST = ["All","Football","Basketball","Cricket","Tennis","Hockey","Volleyball"];

var ROLES = { CUSTOMER:"CUSTOMER", STAFF:"STAFF", ADMIN:"ADMIN" };

// Each sport section — colours and text match the video exactly
var SPORT_SECTIONS = [
  {
    id:"football", label:"FOOTBALL",
    tagline:"Take the pitch.",
    sub:"Tournament-grade footballs, cleats and training kits delivered to your match day.",
    bg:"#0f1f0f", glow:"rgba(34,197,94,0.35)",
    accent:"#4ade80", accentDim:"rgba(74,222,128,0.15)",
    img:"athlete-football.png",
    featured:"Pro Match Football", featuredSub:"FIFA-grade · 12 in stock", price:"8"
  },
  {
    id:"basketball", label:"BASKETBALL",
    tagline:"Own the court.",
    sub:"Indoor balls, portable hoops and gear engineered for the hardwood.",
    bg:"#1a0a0a", glow:"rgba(239,68,68,0.35)",
    accent:"#f87171", accentDim:"rgba(248,113,113,0.15)",
    img:"athlete-basketball.png",
    featured:"Indoor Game Ball", featuredSub:"Composite leather · 18 in stock", price:"7"
  },
  {
    id:"cricket", label:"CRICKET",
    tagline:"Hold the crease.",
    sub:"Grade-1 English willow, pads and helmets — ready when you walk out to bat.",
    bg:"#1a1408", glow:"rgba(234,179,8,0.35)",
    accent:"#fbbf24", accentDim:"rgba(251,191,36,0.15)",
    img:"athlete-cricket.png",
    featured:"English Willow Bat", featuredSub:"Pro weight · 9 in stock", price:"16"
  },
  {
    id:"tennis", label:"TENNIS",
    tagline:"Serve it up.",
    sub:"Tour-spec rackets and championship balls, strung and ready for the baseline.",
    bg:"#171208", glow:"rgba(249,115,22,0.35)",
    accent:"#fb923c", accentDim:"rgba(249,115,22,0.15)",
    img:"athlete-tennis.png",
    featured:"Tour Pro Racket", featuredSub:"Graphite · 11 in stock", price:"12"
  },
  {
    id:"hockey", label:"HOCKEY",
    tagline:"Run the field.",
    sub:"Carbon-composite sticks and full field sets — sharpen your drag-flick game.",
    bg:"#080f18", glow:"rgba(56,189,248,0.35)",
    accent:"#38bdf8", accentDim:"rgba(56,189,248,0.15)",
    img:"athlete-hockey.png",
    featured:"Composite Field Stick", featuredSub:"70% carbon · 14 in stock", price:"11"
  },
  {
    id:"volleyball", label:"VOLLEYBALL",
    tagline:"Rise above.",
    sub:"Indoor balls and full court kits — net, posts, lines. Set up in ten minutes.",
    bg:"#0a0c1a", glow:"rgba(129,140,248,0.35)",
    accent:"#818cf8", accentDim:"rgba(129,140,248,0.15)",
    img:"athlete-volleyball.png",
    featured:"Tournament Volleyball", featuredSub:"FIVB approved · 15 in stock", price:"6"
  }
];

// Equipment catalog data (mock)
var MOCK_EQUIPMENT = [
  {id:"1",  name:"Pro Match Football",    sport:"Football",   price:8,  stock:12, emoji:"⚽", desc:"FIFA-quality match ball with hand-stitched panels."},
  {id:"2",  name:"Football Boots",        sport:"Football",   price:14, stock:15, emoji:"👟", desc:"Lightweight firm-ground boots for precise touch."},
  {id:"3",  name:"Indoor Game Ball",      sport:"Basketball", price:7,  stock:18, emoji:"🏀", desc:"Composite leather basketball for indoor/outdoor use."},
  {id:"4",  name:"Portable Hoop",         sport:"Basketball", price:25, stock:3,  emoji:"🏀", desc:"Height-adjustable hoop with weighted base."},
  {id:"5",  name:"English Willow Bat",    sport:"Cricket",    price:16, stock:9,  emoji:"🏏", desc:"Grade-A English willow, hand-pressed for power."},
  {id:"6",  name:"Cricket Pads",          sport:"Cricket",    price:11, stock:8,  emoji:"🛡️", desc:"Lightweight batting pads with HDF inserts."},
  {id:"7",  name:"Tour Pro Racket",       sport:"Tennis",     price:12, stock:11, emoji:"🎾", desc:"Graphite frame, 300g — balanced for control and power."},
  {id:"8",  name:"Tennis Balls (3-pack)", sport:"Tennis",     price:5,  stock:30, emoji:"🎾", desc:"Pressurized championship-grade balls, tube of 3."},
  {id:"9",  name:"Composite Field Stick", sport:"Hockey",     price:11, stock:14, emoji:"🏑", desc:"70% carbon composite stick with low-bow profile."},
  {id:"10", name:"Hockey Helmet",         sport:"Hockey",     price:16, stock:8,  emoji:"🪖", desc:"Vented helmet with quick-release chin strap."},
  {id:"11", name:"Tournament Volleyball", sport:"Volleyball", price:6,  stock:15, emoji:"🏐", desc:"FIVB-approved 18-panel microfiber volleyball."},
  {id:"12", name:"Volleyball Net Pro",    sport:"Volleyball", price:20, stock:4,  emoji:"🥅", desc:"Official 9.5m tournament net with antenna kit."}
];
