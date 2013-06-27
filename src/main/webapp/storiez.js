
App = Ember.Application.create();

App.Store = DS.Store.extend({
    revision: 12,
    adapter: 'DS.FixtureAdapter'
});

App.Router.map(function() {
  // put your routes here
  // (the index route, for the / path, is automatically inserted)
  this.route('about');
  // this.resource('tests');
  this.resource("storiez", {path: '/storiez'});
  this.resource("storie", {path: '/storie/:storie_id'});
});

/*
App.TestsRoute = Ember.Route.extend({
    model: function() {
        var res = App.Test.find();
        console.log("test count is " + res.length);
        return res;
    }
});

App.Test = DS.Model.extend({
    title: DS.attr('string')
});

App.Test.FIXTURES = [
    { id: 1, title: 'apple'},
    { id: 2, title: 'grape'},
    { id: 3, title: 'nectarine'}
    ];
*/

App.IndexRoute = Ember.Route.extend({
    redirect: function() {
        this.transitionTo('storiez');
    }
});

App.StoriezRoute = Ember.Route.extend({
    model: function() {
        /*
        if (App.allStoriez === null) {
            App.allStoriez = App.StoriezData.create();
            App.allStoriez.loadAll();
        }
        return App.allStoriez;
        */
        var res = App.Storie.find();
        console.log("got storiez");
        return res;
    }
});

/*
App.StoriezController = Ember.ArrayController.extend({
});
*/

App.StorieRoute = Ember.Route.extend({
    model: function(params) {
        /*
        var storie = App.storieStore.findOrCreate(params.storie_id);
        if (App.allStoriez === null) {
            App.allStoriez = App.StoriezData.create();
            App.allStoriez.loadAll();
        }
        return storie;
        */
        return App.Storie.find(params.storie_id);
    },
    renderTemplatexx: function() {
        this._super();
        console.log('StorieRoute.renderTemplate');
    },
    render: function() {
        this._super();
        console.log('StorieRoute.render');
    }
});

App.StorieView = Ember.View.extend({
    render: function(buffer) {
        this._super(buffer);
        console.log('StorieView.render');
    },
    didInsertElement: function() {
        console.log('StorieView.didInsertElement');
        Ember.run.schedule('sync', this, 'processChildElements');
    },
    processChildElements: function() {
        //$('.textSection').css({backgroundColor: 'yellow'});
        var title = this.get('controller').get('title');
        console.log('storie ' + title);
    }
});

App.TextsectionView = Ember.View.extend({
    init: function() {
        this._super();
        this.set('moveInfo', {moving: false});
        this.set('sizeInfo', {sizing: false});
    },
    didInsertElement: function() {
        //console.log('TextsectionView.didInsertElement');
        Ember.run.schedule('sync', this, 'processChildElements');
    },
    processChildElements: function() {
        var title = this.get('controller').get('title');
        var left = this.get('controller').get('left');
        var top = this.get('controller').get('top');
        var width = this.get('controller').get('width');
        var height = this.get('controller').get('height') + 25;
        var jel = this.$().find('.textSection');
        jel.css({left: left, top: top, width: width, height: height});
    },
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
    console.log("run callback");
});

App.Storie = DS.Model.extend({
    title: DS.attr('string'),
    textsections: DS.hasMany('App.Textsection')
});

DS.RESTAdapter.configure('plurals', {
    storie : 'storiez'
});

App.Storie.FIXTURES = [
{ id: 1, title: 'zebras', textsections: [101]},
{ id: 2, title: 'elephants', textsections: [105, 106]}
];

App.Textsection = DS.Model.extend({
    title: DS.attr('string'),
    textcontent: DS.attr('string'),
    left: DS.attr('number'),
    top: DS.attr('number'),
    width: DS.attr('number'),
    height: DS.attr('number'),
    open: DS.attr('boolean'),
    
    storie: DS.belongsTo('App.Storie')
});
App.Textsection.FIXTURES = [
{ id: 101, title: 'stripes', textcontent: 'black and white all over',
  left: 100, top: 120, width: 60, height: 40, open: true},
{ id: 105, title: 'trunk', textcontent: 'very flexible nose',
  left: 120, top: 150, width: 60, height: 40, open: true},
{ id: 106, title: 'tusks', textcontent: 'watch out for these, but they cause the elephants trouble too',
  left: 300, top: 100, width: 160, height: 40, open: true},
];


/*
App.StoriezData = KWEmber.Data.extend({
    init: function() {
        this.set('storiez', []);
    },
    loadAll: function() {
        var self = this;
        $.ajax('/statik/storiez/storiez.json', {
            dataType: 'json',
            success: function(data, status) {
                var stz = data.map(function(dt) {
                    console.log('=== constructing ' + dt.title);
                    // first construct all the sections
                    var sections = dt.sections.map(function(scd) {
                        var sec = App.SectionData.create(scd);
                        console.log('made section ' + sec.get('title'));
                        return sec;
                    });
                    var storie = App.storieStore.findOrCreate(dt.id,
                        {title: dt.title, sections: sections});
                    
                    console.log('finished ' + storie.get('title'));
                    return storie;
                });
                self.set('storiez', stz);
            },
        });
    }
});

App.allStoriez = null;

App.StorieData = KWEmber.Data.extend({
    id: null,
    title: '?',
    sections: [],
    
    init: function() {
        this._super();
    },
    
});

App.storieStore = KWEmber.makeDataStore('id', App.StorieData);

App.SectionData = KWEmber.Data.extend({
    id: null,
    title: '?',
    type: 'text',
    tcontent: '?',
    x: 10,
    y: 10,
    width: 100,
    height: 30,
    open: false,
    bodyStyle: function() {
        var x = this.get('x');
        var y = this.get('y');
        var width = this.get('width');
        var height = this.get('height');
        return 'left:' + x + 'px, top:' + y + 'px, width:' + width + 'px, height:' + height + 'px';
    }.property(['x', 'y', 'width', 'height', 'open'])
});
*/

/*
App.TextsectionController = Ember.ObjectController.extend({
    titleChanged: function() {
        $('.textSection').css({backgroundColor: 'green'});
    }.observes('title')
});
*/
