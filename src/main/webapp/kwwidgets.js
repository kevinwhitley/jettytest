App.KWEditView = Ember.View.extend({
    init: function() {
        this._super();
    },
    templateName: 'KWEdit',
    classNames: ['kwedit'],
    editText: function(key, value) {
        if (arguments.length === 1) { // getter
            return this.get('_editText') || this.get('txt');
        }
        else { // setter
            this.set('_editText', value);
        }
    }.property('txt'),
    contextMenu: function(event) {
        //console.log('context menu ' + this.get('txt'));
        var isEditing = !this.get('isEditing');
        if (!isEditing) {
            var newText = this.get('_editText');
            if (newText !== this.get('txt')) {
                this.get('fn')(newText);
            }
        }
        this.set('isEditing', isEditing);
        return false;
    }
});

App.KWEditViewArea = Ember.View.extend({
    init: function() {
        this._super();
    },
    templateName: 'KWEditArea',
    classNames: ['kweditarea'],
    classNameBindings: ['isEditing'],
    editText: function(key, value) {
        if (arguments.length === 1) { // getter
            return this.get('_editText') || this.get('txt');
        }
        else { // setter
            this.set('_editText', value);
        }
    }.property('txt'),
    contextMenu: function(event) {
        //console.log('area context menu ' + this.get('isEditing'));
        var isE = !this.get('isEditing');
        this.set('isEditing', isE);
        if (isE) {
            var ta = this.$().find('textarea');
            console.log('area: ' + ta);
            ta.focus();
            var len = this.get('txt').length;
            setTextSelection(ta[0], len, len);
        }
        else {
            var newText = this.get('_editText');
            if (newText !== this.get('txt')) {
                this.get('fn')(newText);
            }
        }
        event.stopPropagation();
        event.preventDefault();
        return false;
    },
    isDisabled: function(key, value) {
        if (arguments.length === 1) { // getter
            return !this.get('isEditing');
        }
        else { // setter
            this.set('isEditing', !value);
        }
    }.property('isEditing')
});

function setTextSelection(domE, start, end)
{
    if (domE.setSelectionRange) {
        domE.setSelectionRange(start, end);
    }
    else if (domE.createTextRange) {
      var range = domE.createTextRange();
      range.collapse(true);
      range.moveEnd('character', start);
      range.moveStart('character', end);
      range.select();
    }
}
