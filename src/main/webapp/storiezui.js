
App = Ember.Application.create();

App.storiez = new Storiez();

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
    /*
    rollbackData: function() {
        var stoor = this.get('store');
        stoor.get('defaultTransaction').rollback();
    },
    */
    executeUndo: function() {
        App.storiez.undo();
    },
    executeRedo: function() {
        App.storiez.redo();
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
        //console.log('StorieController.init');
    }
});

App.StorieRoute = Ember.Route.extend({
    model: function(params) {
        // this is only called when we go directly (by url) to the storie
        // clicking on a link from storiez page does not get us here
        var storie = App.Storie.find(params.storie_id);
        return storie;
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
        //console.log('storie ' + title);
    }
});

App.TextsectiondispController = Ember.ObjectController.extend({
    init: function() {
        this._super();
        //console.log('TextsectiondispController.init');
    }
});

App.TextsectiondispView = Ember.View.extend({
    init: function() {
        this._super();
        this.set('moveInfo', {moving: false});
        this.set('sizeInfo', {sizing: false});
    },
    classNames: ['textSection'],
    // didInsertElement is called when the elements are created
    // note that if the storie knows about the textdisps (so the elements are created) but
    // the disps aren't loaded yet, then we will be called here when there is no data
    //---
    // however, we still need this call to didInsertElement.  Think about when we
    // have loaded the view, go to a different page, then return to the view.  In that case
    // the model values don't change, so the observers for fixGeometry don't fire.  But we'll
    // still be inserting the elements, so we can reshape that way
    didInsertElement: function() {
        //console.log('TextsectionView.didInsertElement');
        Ember.run.schedule('sync', this, 'reshapeElement');
    },
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
            //console.log('reshapeElement');
            this.$().css({left: left, top: top, width: width, height: height});
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
        var moveInfo = this.get('moveInfo');
        this.moveMouseMove(event);
        var geometry = this._moveGeometry(event, moveInfo);
        moveInfo.moving = false;
        var controller = this.get('controller');
        App.storiez.reshape(controller, this.$(), {
            left: geometry.left,
            top: geometry.top,
            width: controller.get('width'),
            height: controller.get('height')
        });
        return false;
    },
    moveMouseMove: function(event) {
        var moveInfo = this.get('moveInfo');
        if (moveInfo.moving) {
            var geometry = this._moveGeometry(event, moveInfo);
            //var controller = this.get('controller');
            //controller.set('left', left);
            //controller.set('top', top);
            this.$().css({left: geometry.left, top: geometry.top});
        }
        return false;
    },
    _moveGeometry: function(event, moveInfo) {
        return {
            left: moveInfo.originalLeft + event.pageX - moveInfo.downLeft,
            top: moveInfo.originalTop + event.pageY - moveInfo.downTop
        };
    },
    sizeMouseUp: function(event) {
        $('body').off('mouseup.StorieSize mousemove.StorieSize', null);
        var sizeInfo = this.get('sizeInfo');
        this.sizeMouseMove(event);
        sizeInfo.sizing = false;
        var geometry = this._sizeGeometry(event, sizeInfo);
        var controller = this.get('controller');
        App.storiez.reshape(controller, this.$(), {
            left: controller.get('left'),
            top: controller.get('top'),
            width: geometry.width,
            height: geometry.height
        });
        return false;
    },
    sizeMouseMove: function(event) {
        var sizeInfo = this.get('sizeInfo');
        if (sizeInfo.sizing) {
            var geometry = this._sizeGeometry(event, sizeInfo);
            this.$().css({width: geometry.width, height: geometry.height});
        }
        return false;
    },
    _sizeGeometry: function(event, sizeInfo) {
        return {
            width: sizeInfo.originalWidth + event.pageX - sizeInfo.downLeft,
            height: sizeInfo.originalHeight + event.pageY - sizeInfo.downTop
        };
    }
});

Ember.run(function(){
    console.log("Ember.run callback");
});

