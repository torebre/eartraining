

module.exports = (function() {
  function Note(note, duration) {
    this.init(note, duration);
  }


  Note.prototype = {

    init: function(note, duration) {
      this.note = note;
      this.duration = duration;
    },

    getNote: function() {
      return this.note;
    },

    getDuration: function() {
      return this.duration;
    }

  }

  return Note;

})();
