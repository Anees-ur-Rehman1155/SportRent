// components/ui/scrollStory.js
// Builds the full-screen scroll-jacked sport sections exactly as shown in the video.
// Each sport section is a full-viewport panel that cross-fades on scroll.

function initScrollStory() {
  var wrapper = document.getElementById("scrollStoryWrapper");
  if (!wrapper) return;

  api.listEquipment().then(function(items) {
    // update SPORT_SECTIONS with current database/mock values
    for (var idx = 0; idx < SPORT_SECTIONS.length; idx++) {
      var s = SPORT_SECTIONS[idx];
      var match = items.filter(function(it) {
        return it.name.toLowerCase() === s.featured.toLowerCase() || String(it.id) === String(idx + 1);
      })[0];
      if (match) {
        s.price = match.price;
        var prefix = s.featuredSub.split("·")[0].trim();
        s.featuredSub = prefix + " · " + (match.stock > 0 ? match.stock + " in stock" : "Sold out");
      }
    }
    renderStory();
  }).catch(function() {
    renderStory();
  });

  function renderStory() {
    var N = SPORT_SECTIONS.length; // 6 sports

    // ── Build HTML ────────────────────────────────────────────────────────
    // Outer container height = N * 100vh  so the user scrolls through each sport
    var scrollContainer = document.createElement("div");
    scrollContainer.className = "sport-scroll-container";
    scrollContainer.style.height = (N * 100) + "vh";

    // One sticky layer that stays at top=0 the whole time
    var stickyLayer = document.createElement("div");
    stickyLayer.className = "sport-sticky";

    // Build each sport panel inside the sticky layer
    for (var i = 0; i < N; i++) {
      var s = SPORT_SECTIONS[i];
      var panel = document.createElement("div");
      panel.className = "sport-panel" + (i === 0 ? " active" : "");
      panel.id = "sport-panel-" + i;
      panel.style.background = s.bg;

      // Radial glow behind the athlete
      panel.style.backgroundImage =
        "radial-gradient(ellipse 700px 700px at 50% 60%, " + s.glow + ", transparent 70%), " +
        "linear-gradient(180deg, " + s.bg + " 0%, #000 100%)";

      panel.innerHTML =
        // Left: text column
        '<div>' +
          '<div class="sport-label-pill" style="color:' + s.accent + ';border-color:' + s.accent + '60">' +
            '<span class="sport-label-dot" style="background:' + s.accent + '"></span>' +
            s.label +
          '</div>' +
          '<div class="sport-tagline">' + s.tagline + '</div>' +
          '<div class="sport-sub">' + s.sub + '</div>' +
        '</div>' +
        // Center: athlete image
        '<div class="sport-athlete-col">' +
          '<img class="sport-athlete-img" src="' + ROOT + 'src/assets/images/' + s.img + '" alt="' + s.label + ' athlete">' +
        '</div>' +
        // Right: featured product card
        '<div class="sport-card-col">' +
          '<div class="featured-card" style="background:' + s.accentDim + '">' +
            '<div class="featured-label">Featured</div>' +
            '<div class="featured-name">' + s.featured + '</div>' +
            '<div class="featured-sub">' + s.featuredSub + '</div>' +
            '<div class="featured-from">From</div>' +
            '<div class="featured-price">$' + s.price + ' <span>/ day</span></div>' +
            '<a href="' + ROOT + 'src/pages/catalog/catalog.html?sport=' + s.id + '" class="rent-btn">Rent</a>' +
          '</div>' +
        '</div>';

      stickyLayer.appendChild(panel);
    }

    scrollContainer.appendChild(stickyLayer);
    wrapper.appendChild(scrollContainer);

    // ── Build dot nav ─────────────────────────────────────────────────────
    var dotNav = document.getElementById("dotNav");
    dotNav.innerHTML = ""; // clear previous if any
    for (var j = 0; j < N; j++) {
      var dot = document.createElement("div");
      dot.className = "dot-nav-item" + (j === 0 ? " active" : "");
      dot.dataset.idx = j;
      (function(idx) {
        dot.onclick = function() { scrollToSport(idx); };
      })(j);
      dotNav.appendChild(dot);
    }

    // ── Scroll to sport by index ──────────────────────────────────────────
    function scrollToSport(idx) {
      var wrapperTop = wrapper.getBoundingClientRect().top + window.scrollY;
      var sectionH   = window.innerHeight;
      window.scrollTo({ top: wrapperTop + idx * sectionH, behavior: "smooth" });
    }

    // ── Update on scroll ──────────────────────────────────────────────────
    var currentIdx = -1;

    function onScroll() {
      var wrapperTop    = wrapper.getBoundingClientRect().top + window.scrollY;
      var scrolled      = window.scrollY - wrapperTop;
      var sectionH      = window.innerHeight;
      var totalH        = N * sectionH;

      // Are we inside the scroll story?
      var inStory = scrolled >= 0 && scrolled < totalH;
      dotNav.classList.toggle("hidden", !inStory);

      if (!inStory) return;

      // Which section are we on?
      var idx = Math.min(N - 1, Math.floor(scrolled / sectionH));

      if (idx === currentIdx) return;
      currentIdx = idx;

      // Activate correct panel
      var panels = stickyLayer.querySelectorAll(".sport-panel");
      for (var k = 0; k < panels.length; k++) {
        panels[k].classList.toggle("active", k === idx);
      }

      // Activate correct dot
      var dots = dotNav.querySelectorAll(".dot-nav-item");
      for (var m = 0; m < dots.length; m++) {
        dots[m].classList.toggle("active", m === idx);
      }
    }

    window.addEventListener("scroll", onScroll, { passive: true });
    onScroll(); // run once on load
  }
}
