App.Storie = DS.Model.extend({
    title: DS.attr('string'),
    textsectiondisps: DS.hasMany('App.Textsectiondisp'),

    // client-side only values (will get default initial values when created)
    titleEdit: DS.attr('boolean')
});

DS.RESTAdapter.configure('plurals', {
    storie : 'storiez'
});

App.Textsectiondisp = DS.Model.extend({
    left: DS.attr('number'),
    top: DS.attr('number'),
    width: DS.attr('number'),
    height: DS.attr('number'),
    open: DS.attr('boolean'),
    storie: DS.belongsTo('App.Storie'),
    textsection: DS.belongsTo('App.Textsection'),
    
    // client-side only values (will get default initial values when created)
    titleEdit: DS.attr('boolean'),
    contentEdit: DS.attr('boolean')
    
});

App.Textsection = DS.Model.extend({
    title: DS.attr('string'),
    textcontent: DS.attr('string')
});
