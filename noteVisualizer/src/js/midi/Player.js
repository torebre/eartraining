


module.exports = (function() {
  function Player() {
    this.init();

  }


  var sampleRate = 44100;


  Player.prototype = {

    init: function() {
      console.log("Requesting MIDI access: " +navigator.requestMIDIAccess);

      var webkitAudio = window.AudioContext || window.webkitAudioContext;
      console.log("Webkit audio: " +webkitAudio);


      var context = new webkitAudio();

      /*
      var source = context.createBufferSource();

      var count = 1000;
      var input = new Array(count * 2);
      var sineGen = SineGenerator(440);

      sineGen.generate(input, 0, count);
      source.buffer = input;
      source.connect(context.destination);
      source.start(0);
      */


      //sampleRate = context.sampleRate;

      var channelCount = 2;
      var bufferSize = 4096*4; // Higher for less gitches, lower for less latency

      var node = context.createScriptProcessor(bufferSize, 0, channelCount);

      var generator = SineGenerator(440);



      node.onaudioprocess = function(e) { process(e); };

      function process(e) {
        if (generator.finished) {
          node.disconnect();
          return;
        }

        var dataLeft = e.outputBuffer.getChannelData(0);
        var dataRight = e.outputBuffer.getChannelData(1);

        var generate = generator.generate(bufferSize);

        for (var i = 0; i < bufferSize; ++i) {
          dataLeft[i] = generate[i*2];
          dataRight[i] = generate[i*2+1];
        }
      }

      // start
      node.connect(context.destination);

      /*
      return {
      'stop': function() {
      // pause
      node.disconnect();
      requestStop = true;
    },
    'type': 'Webkit Audio'
  }
  */

},

play: function(note) {



}





  };


  PianoProgram = {
    'attackAmplitude': 0.2,
    'sustainAmplitude': 0.1,
    'attackTime': 0.02,
    'decayTime': 0.3,
    'releaseTime': 0.02,
    'createNote': function(note, velocity) {
      var frequency = midiToFrequency(note);
      return ADSRGenerator(
        SineGenerator(frequency),
        this.attackAmplitude * (velocity / 128), this.sustainAmplitude * (velocity / 128),
        this.attackTime, this.decayTime, this.releaseTime
      );
    }
  };

  function ADSRGenerator(child, attackAmplitude, sustainAmplitude, attackTimeS, decayTimeS, releaseTimeS) {
    var self = {'alive': true};
    var attackTime = sampleRate * attackTimeS;
    var decayTime = sampleRate * (attackTimeS + decayTimeS);
    var decayRate = (attackAmplitude - sustainAmplitude) / (decayTime - attackTime);
    var releaseTime = null; /* not known yet */
    var endTime = null; /* not known yet */
    var releaseRate = sustainAmplitude / (sampleRate * releaseTimeS);
    var t = 0;

    self.noteOff = function() {
      if (self.released) return;
      releaseTime = t;
      self.released = true;
      endTime = releaseTime + sampleRate * releaseTimeS;
    };

    self.generate = function(buf, offset, count) {
      if (!self.alive) return;
      var input = new Array(count * 2);
      for (var i = 0; i < count*2; i++) {
        input[i] = 0;
      }
      child.generate(input, 0, count);

      childOffset = 0;
      while(count) {
        if (releaseTime != null) {
          if (t < endTime) {
            /* release */
            while(count && t < endTime) {
              var ampl = sustainAmplitude - releaseRate * (t - releaseTime);
              buf[offset++] += input[childOffset++] * ampl;
              buf[offset++] += input[childOffset++] * ampl;
              t++;
              count--;
            }
          } else {
            /* dead */
            self.alive = false;
            return;
          }
        } else if (t < attackTime) {
          /* attack */
          while(count && t < attackTime) {
            var ampl = attackAmplitude * t / attackTime;
            buf[offset++] += input[childOffset++] * ampl;
            buf[offset++] += input[childOffset++] * ampl;
            t++;
            count--;
          }
        } else if (t < decayTime) {
          /* decay */
          while(count && t < decayTime) {
            var ampl = attackAmplitude - decayRate * (t - attackTime);
            buf[offset++] += input[childOffset++] * ampl;
            buf[offset++] += input[childOffset++] * ampl;
            t++;
            count--;
          }
        } else {
          /* sustain */
          while(count) {
            buf[offset++] += input[childOffset++] * sustainAmplitude;
            buf[offset++] += input[childOffset++] * sustainAmplitude;
            t++;
            count--;
          }
        }
      }
    }

    return self;
  }

  function SineGenerator(freq) {
    var self = {'alive': true};
    var period = sampleRate / freq;
    var t = 0;


    self.generate2 = function(buf, offset, count) {
      for (; count; count--) {
        var phase = t / period;
        var result = Math.sin(phase * 2 * Math.PI);
        buf[offset++] += result;
        buf[offset++] += result;
        t++;
      }
    }

    self.generateIntoBuffer = function(samplesToGenerate, buffer, offset) {
      for (var i = offset; i < offset + samplesToGenerate * 2; i++) {
        buffer[i] = 0;
      }
      self.generate2(buffer, offset, samplesToGenerate);
    }

    self.generate = function(samples) {
      var data = new Array(samples*2);
      self.generateIntoBuffer(samples, data, 0);
      return data;
    }
    return self;
  }

  return Player;

})();
