KWEmber = {};

KWEmber.Data = Ember.Object.extend({
    _stub: false,  // set to true by data store if the object is a key-stub
    
    init: function() {
        this._rememberOriginal();
    },
    
    _rememberOriginal: function() {
        this._originals = {};
        this._changed = false;
        
        var self = this;
        if (this.watchVars) {
            /*
            $.each(this.watchVars, function(ix, wt) {
                if (typeof(wt) !== 'functionxx') {
                    console.log('remembering value of ' + wt + ' ' + typeof(wt));
                    self._originals[wt] = self.get(wt);
                    console.log('     remembered');
                    //self.addObserver(wt, self, self.valueChanged);
                }
            });
            */
            this._originals = this.watchVars.map(function(dt) {
                //console.log('remembering ' + dt);
                return dt;
            });
        }
    },
    
    /*
    valueChanged: function(sender, key) {
        var newValue = this.get(key);  // value that we have now (after change)
        console.log('color changed 1 ' + sender);
        console.log('color changed 2 ' + key);
    },
    */
    
    // need to enhance so that changes to arrays (length or order) can be detected
    // and what about object elements?
    hasChanged: function() {
        var changed = false;
        var self = this;
        $.each(this.watchVars, function(ix, wt) {
            changed = changed || (self._originals[wt] !== self.get(wt));
        });
        
        return changed;
    },

    save: function() {
        // refresh the original values
        // this should be managed with proper state change
        // need to deal with changes happening while we're waiting for server response
        // and also need to handle errors coming back from server
        this._rememberOriginal();
    }

});

KWEmber.LazyObject = KWEmber.Data.extend({
    lazyData: null,
    
    useLazy: function(key) {
        var lazy = this.get('lazyData');
        if (!lazy) {
            this.fetchData();
            return null;
        }
        else {
            //console.log('using lazy data for ' + key);
            return lazy[key];
        }
    },
    
    dataLoaded: function() {
        return this.lazyData !== null;
    }.property('lazyData'),
    
    tickle: function() {
        if (this.lazyData === null) {
            this.fetchData();
        }
        return null;
    }.property('lazyData'),
    
    fetchData: function() {
        console.log('Need to override fetchData');
        return null;
    },
    
    loadedLazyData: function(data) {
        this.set('lazyData', data);
    }
    
});

// create a simple data store of KWEmber.Data objects
// the object is looked up by a simple key
// if it isn't present, the key is used to create a stub instance
KWEmber.makeDataStore = function(keyName, classO) {
    var store = {objects: {}};
    
    // look up the object
    // if not found, create a stub based on the key
    //    and if there is createData, fill it out and we have a non-stub object
    // if found and there is create data
    //    if it is not a stub - do nothing
    //    if it is a stub - fill out and reset the "original" data, and clear stub
    
    store.findOrCreate = function(key, createData) {
        var obj = this.objects[key];
        if (!obj) {
            var initialData = {};
            if (createData) {
                $.extend(initialData, createData);
            }
            // always override the handed-in key value (don't use what may be in createData)
            initialData[keyName] = key;
            
            obj = classO.create(initialData);
            if (createData) {
                obj._rememberOriginal();
            }
            else {
                obj._stub = true;
            }
            this.objects[key] = obj;
        }
        else if (createData && obj._stub) {
            // fill out the object with the createData
            $.each(createData, function(vkey, value) {
                obj.set(vkey, value);
            });
            obj._rememberOriginal();
            obj._stub = false;
        }
        return obj;
    };
    
    // look up the object, returning null if not found
    store.find = function(key) {
        return this.objects[key] || null;
    };
    
    return store;
};

KWEmber.makeFetchAllDataStore = function(keyName, classO, classAll) {
    var store = null;
    
    store.find = function(key) {
        return this.objects[key] || null;
    };
}

