var Symbols = require("./symbols.js");
var Font = require("./font2.js");


module.exports = (function() {
  function GlyphFactory() {

  }

  GlyphFactory.getGlyph = function(glyphType) {
    return Font[glyphType.glyph].d;
  };

  GlyphFactory.getBoundingBox = function() {
    return {
      width: 852.632 + 510.142,
      height: 1487.78 + 1370.06
    };
  };

  GlyphFactory.getScaledBoundingBox = function(scale) {
    var boundingBox = GlyphFactory.getBoundingBox();
    return {
      width: boundingBox.width * scale,
      height: boundingBox.height * scale
    };
  };

  GlyphFactory.getCenter = function(scale) {
    var boundingBox = GlyphFactory.getBoundingBox();

    //var scaledWidth = boundingBox.width * scale;
    //var scaledHeight = boundingBox.height * scale;

    //console.log("Scaled width: " +scaledWidth +". Scaled height: " +scaledHeight);

    var diag = Math.sqrt(Math.pow(0.5 * boundingBox.width, 2) + Math.pow(0.5 * boundingBox.height, 2));
    var angle = Math.atan((0.5 * boundingBox.height) / (0.5 * boundingBox.width));
    var temp = scale * diag;

    return {
      x: Math.cos(angle) * temp,
      y: Math.sin(angle) * temp
    };
  };

  return GlyphFactory;

})();
