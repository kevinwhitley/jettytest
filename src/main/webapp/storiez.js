
App = Ember.Application.create();

App.Store = DS.Store.extend({
    init: function() {
        this._super();
    },
    //revision: 12,
    //adapter: 'DS.FixtureAdapter'
});

App.Router.map(function() {
  // put your routes here
  // (the index route, for the / path, is automatically inserted)
  this.route('about');
  // this.resource('tests');
  this.resource("storiez", {path: '/storiez'});
  this.resource("storie", {path: '/storie/:storie_id'});
});

App.ApplicationController = Ember.Controller.extend({
    commitData: function() {
        var stoor = this.get('store');
        stoor.commit();
    },
    rollbackData: function() {
        var stoor = this.get('store');
        stoor.get('defaultTransaction').rollback();
    }
});

App.IndexRoute = Ember.Route.extend({
    redirect: function() {
        this.transitionTo('storiez');
    }
});

App.StoriezRoute = Ember.Route.extend({
    model: function() {
        var res = App.Storie.find();
        //console.log("got storiez");
        return res;
    }
});

/*
App.StoriezController = Ember.ArrayController.extend({
});
*/

App.StorieController = Ember.ObjectController.extend({
    init: function() {
        this._super();
        //var md = this.get('model'); -- returns null
        //var vw = this.get('view'); -- returns null
        console.log('StorieController.init');
    },
    editStorie: function() {
        console.log('edit storie');
        this.set('title', this.get('title') + 'hoho!');
    }
});

App.StorieRoute = Ember.Route.extend({
    model: function(params) {
        // this is only called when we go directly (by url) to the storie
        // clicking on a link from storiez page does not get us here
        return App.Storie.find(params.storie_id);
    },
    renderTemplatexx: function() {
        this._super();
        //console.log('StorieRoute.renderTemplate');
    },
    render: function() {
        this._super();
        //console.log('StorieRoute.render');
    }
});

App.StorieView = Ember.View.extend({
    render: function(buffer) {
        this._super(buffer);
        //console.log('StorieView.render');
    },
    didInsertElement: function() {
        //console.log('StorieView.didInsertElement');
        //Ember.run.schedule('sync', this, 'reshapeElement');
    },
    reshapeElement: function() {
        //$('.textSection').css({backgroundColor: 'yellow'});
        var title = this.get('controller').get('title');
        console.log('storie ' + title);
    }
});

App.KWEditView = Ember.View.extend({
    init: function() {
        this._super();
    },
    templateName: 'KWEdit',
    contextMenu: function(event) {
        //console.log('context menu ' + this.get('txt'));
        this.set('doedit', !this.get('doedit'));
        return false;
    }
});

App.TextsectiondispController = Ember.ObjectController.extend({
    init: function() {
        this._super();
        console.log('TextsectiondispController.init');
        //this.set('marker', 'disp controller hey');
    },
    startTitleEdit: function() {
        this.set('titleEdit', true);
    },
    stopTitleEdit: function() {
        this.set('titleEdit', false);
    },
    startContentEdit: function() {
        this.set('contentEdit', true);
    },
    stopContentEdit: function() {
        this.set('contentEdit', false);
    },
    checkGeometry: function() {
        console.log('TextsectiondispController.checkGeometry').observes('left');
    }
});

App.TextsectiondispView = Ember.View.extend({
    init: function() {
        this._super();
        this.set('moveInfo', {moving: false});
        this.set('sizeInfo', {sizing: false});
    },
    // didInsertElement is called when the elements are created
    // note that if the storie knows about the textdisps (so the elements are created) but
    // the disps aren't loaded yet, then we will be called here when there is no data
    //didInsertElement: function() {
        //console.log('TextsectionView.didInsertElement');
        //Ember.run.schedule('sync', this, 'reshapeElement');
    //},
    reshapeElement: function() {
        //console.log("Textsectiondispview.reshapeElement");
        // only do this work if the actual html is created
        // (we may have inserted the 'element' but not created any html)
        if (this.$()) {
            var cnt = this.get('controller');
            var marker = cnt.get('marker');
            var left = cnt.get('left');
            var top = cnt.get('top');
            var width = cnt.get('width');
            var height = cnt.get('height') + 25;
            var jel = this.$().find('.textSection');
            jel.css({left: left, top: top, width: width, height: height});
        }
    },
    // when the geometry properties change, reshape the element
    fixGeometry: function() {
        //console.log('TextsectiondispView.fixGeometry');
        this.reshapeElement();
    }.observes('controller.left').observes('controller.top').observes('controller.width').observes('controller.height'),
    mouseDown: function(event) {
        //console.log('mousedown');
        //console.log('target: ' + event.target);
        if (event.target === this.$().find('.textSectionMover')[0]) {
            this.set('moveInfo', {
                originalLeft: this.get('controller').get('left'),
                originalTop: this.get('controller').get('top'),
                downLeft: event.pageX,
                downTop: event.pageY,
                moving: true
            });
            // listen for mouse events on whole document (so moves don't get lost)
            // we aren't using ember event handlers on the view because the user can get
            // the mouse outside the view (but we still want to track it)
            // and also so that we only have the mousemove handler installed when needed
            // (and also to improve performance)
            var self = this;
            $('body').on('mouseup.StorieMove', null, function(event) {return self.moveMouseUp(event);});
            $('body').on('mousemove.StorieMove', null, function(event) {return self.moveMouseMove(event);});
        }
        else if (event.target === this.$().find('.textSectionSizer')[0]) {
            this.set('sizeInfo', {
                originalWidth: this.get('controller').get('width'),
                originalHeight: this.get('controller').get('height'),
                downLeft: event.pageX,
                downTop: event.pageY,
                sizing: true
            });
            var self = this;
            $('body').on('mouseup.StorieSize', null, function(event) {return self.sizeMouseUp(event);});
            $('body').on('mousemove.StorieSize', null, function(event) {return self.sizeMouseMove(event);});
        }
        return true;
    },
    moveMouseUp: function(event) {
        $('body').off('mouseup.StorieMove mousemove.StorieMove', null);
        this.moveMouseMove(event);
        this.get('moveInfo').moving = false;
        return true;
    },
    moveMouseMove: function(event) {
        var moveInfo = this.get('moveInfo');
        if (moveInfo.moving) {
            var left = moveInfo.originalLeft + event.pageX - moveInfo.downLeft;
            var top = moveInfo.originalTop + event.pageY - moveInfo.downTop;
            var jel = this.$().find('.textSection');
            var controller = this.get('controller');
            controller.set('left', left);
            controller.set('top', top);
            jel.css({left: left, top: top});
        }
        return true;
    },
    sizeMouseUp: function(event) {
        $('body').off('mouseup.StorieSize mousemove.StorieSize', null);
        this.sizeMouseMove(event);
        this.get('sizeInfo').sizing = false;
        return true;
    },
    sizeMouseMove: function(event) {
        var sizeInfo = this.get('sizeInfo');
        if (sizeInfo.sizing) {
            var width = sizeInfo.originalWidth + event.pageX - sizeInfo.downLeft;
            var height = sizeInfo.originalHeight + event.pageY - sizeInfo.downTop;
            var jel = this.$().find('.textSection');
            var controller = this.get('controller');
            controller.set('width', width);
            controller.set('height', height);
            jel.css({width: width, height: height});
        }
        return true;
    }
});

Ember.run(function(){
    console.log("Ember.run callback");
});

