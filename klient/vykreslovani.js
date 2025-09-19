
//od chatgpt
function odehratKartu(element,jeToMojeKarta) {
  const canvas = (typeof odhazovaciBalicek !== 'undefined')
    ? odhazovaciBalicek
    : document.getElementById('odhazovaciBalicek');

  if (!canvas || !(canvas instanceof HTMLCanvasElement)) {
    throw new Error('Canvas "odhazovaciBalicek" nenalezen.');
  }

  const ctx = canvas.getContext('2d');

  function vykresli(img,jeToMojeKarta) {
    const origW = img.naturalWidth || img.width;
    const origH = img.naturalHeight || img.height;

    const targetW = 112;
    const targetH = origH * (targetW / origW);

    const cx = canvas.width / 2;
    const cy = canvas.height / 2;

    const dx = nahodneCislo(-30, 30);
    const dy = nahodneCislo(-30, 30);
    const deg = nahodneCislo(-30, 30);
    const rad = deg * Math.PI / 180;

    ctx.save();
    ctx.translate(cx + dx, cy + dy);
    ctx.rotate(rad);

    // nastavení stínu
    ctx.shadowColor = "black";
    ctx.shadowOffsetX = -2;
    ctx.shadowOffsetY = 2;
    ctx.shadowBlur = 2;

    // vytvoření cesty s kulatými rohy
    const r = 8; // radius rohů
    const x = -targetW / 2;
    const y = -targetH / 2;
    const w = targetW;
    const h = targetH;

    ctx.beginPath();
    if (ctx.roundRect) {
      // moderní API (Chrome, FF)
      ctx.roundRect(x, y, w, h, r);
    } else {
      // fallback (pro starší prohlížeče)
      ctx.moveTo(x + r, y);
      ctx.lineTo(x + w - r, y);
      ctx.quadraticCurveTo(x + w, y, x + w, y + r);
      ctx.lineTo(x + w, y + h - r);
      ctx.quadraticCurveTo(x + w, y + h, x + w - r, y + h);
      ctx.lineTo(x + r, y + h);
      ctx.quadraticCurveTo(x, y + h, x, y + h - r);
      ctx.lineTo(x, y + r);
      ctx.quadraticCurveTo(x, y, x + r, y);
    }
    ctx.closePath();

    ctx.clip(); // ořízne na zaoblený obdélník
    ctx.drawImage(img, x, y, w, h);

    ctx.restore();
  }

  if (typeof element === 'string') {
    const img = new Image();
    img.src = element;
    if (img.complete) {
      vykresli(img,jeToMojeKarta);
    } else {
      img.onload = () => vykresli(img,jeToMojeKarta);
    }
  } else if (element instanceof HTMLImageElement) {
    if (element.complete && element.naturalWidth !== 0) {
      vykresli(element,jeToMojeKarta);
    } else {
      element.addEventListener('load', function onLoad() {
        element.removeEventListener('load', onLoad);
        vykresli(element,jeToMojeKarta);
      });
    }
  } else {
    throw new TypeError('Parametr "element" musí být HTMLImageElement nebo string (src).');
  }

  if(jeToMojeKarta){
      //FIX: tohle se musí dělat někde jinde
    socket.send("odehrani:"+element.getAttribute("data-id"))
    
    element.remove();
  }

}


function vytvorHracElement(hrac) {
    // hlavní wrapper
    const wrapper = document.createElement("div");
    wrapper.className = "hracuvobal";
    wrapper.dataset.id = hrac.id;
    wrapper.dataset.maximumZivotu = hrac.maximumZivotu;

    // jméno hráče
    const jmeno = document.createElement("span");
    jmeno.className = "jmeno";
    jmeno.textContent = hrac.jmeno;
    wrapper.appendChild(jmeno);

   /* // životy
    const zivoty = document.createElement("span");
    zivoty.className = "zivoty";
    zivoty.textContent = hrac.zivoty + "/" + hrac.maximumZivotu;
    wrapper.appendChild(zivoty);
   */
    // postava
    const postava = document.createElement("img");
    postava.className = "karta postava";
    postava.src = "img/karty/postavy/" + hrac.postava + ".png";
    wrapper.appendChild(postava);

    // postava
    const obrazekZivotu = document.createElement("img");
    obrazekZivotu.className = "karta";
    obrazekZivotu.src = "img/velkeZivoty/" + hrac.zivoty + "zivoty.png";
    wrapper.appendChild(obrazekZivotu);

    // malé karty
    const maleKarty = document.createElement("img");
    maleKarty.className = "malekarty";
    maleKarty.src = "img/maleKarty/" + hrac.pocetKaret + ".png";
    wrapper.appendChild(maleKarty);

    return wrapper;
}
