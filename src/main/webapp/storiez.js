
function Storiez()
{
    console.log('new storiez object');
    this._clearUndos();
}

Storiez.prototype._clearUndos = function()
{
    this._undoStack = [];
    this._undoIndex = -1; // points at operation next "undo" should undo
    this._undoLast = -1;  // points at last operation performed
};

Storiez.prototype.undo = function()
{
    if (this._undoIndex < 0) {
        return;
    }

    var undo = this._undoStack[this._undoIndex--];
    undo.runUndo();
};

Storiez.prototype.redo = function()
{
    if (this._undoIndex >= this._undoLast) {
        return;
    }

    var undo = this._undoStack[this._undoIndex+1];
    this._undoIndex++;
    undo.runRedo();
};

// action to simply change one value on an object with no special side effects
Storiez.prototype.changeValue = function(obj, key, value)
{
    var oldValue = obj.get(key);
    var self = this;
    var undo = {
        runUndo: function() {self._opSetValue(obj, key, oldValue);},
        runRedo: function() {self._opSetValue(obj, key, value);}
    };
    this._doAction(undo);
};

// action to change the geometry of an object that has left, top, width, height properties
Storiez.prototype.reshape = function(obj, jel, geometry)
{
    var oldGeometry = {
        left: obj.get('left'),
        top: obj.get('top'),
        width: obj.get('width'),
        height: obj.get('height')
    }
    
    var self = this;
    var undo = {
        runUndo: function() {self._opReshape(obj, jel, oldGeometry);},
        runRedo: function() {self._opReshape(obj, jel, geometry);}
    }
    
    this._doAction(undo);
};

// execute an action - should be passed an undo object which has two closures:
// runRedo - to make the action happen (including for the first time)
// runUndo - to undo the action
Storiez.prototype._doAction = function(undo)
{
    // save it
    this._undoStack[++this._undoIndex] = undo;
    this._undoLast = this._undoIndex;

    undo.runRedo();
};

Storiez.prototype._opSetValue = function(obj, key, value)
{
    obj.set(key, value);
};

Storiez.prototype._opReshape = function(obj, jel, geometry)
{
    obj.set('left', geometry.left);
    obj.set('top', geometry.top);
    obj.set('height', geometry.height);
    obj.set('width', geometry.width);
    jel.css({left: geometry.left, top: geometry.top, width: geometry.width, height: geometry.height});
};

