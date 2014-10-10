Ext.onReady( function() {

	// CUSTOM

	/*----------------------------------------------------------------------------
	 * EXTJS UX custom
	 *--------------------------------------------------------------------------*/

	/* ColorButton */
	Ext.define('Ext.ux.button.ColorButton', {
		extend: 'Ext.button.Button',
		alias: 'widget.colorbutton',
		width: 109,
		height: 22,
		defaultValue: null,
		value: 'f1f1f1',
		getValue: function() {
			return this.value;
		},
		setValue: function(color) {
			this.value = color;
			if (Ext.isDefined(this.getEl())) {
				this.getEl().dom.style.background = '#' + color;
			}
		},
		reset: function() {
			this.setValue(this.defaultValue);
		},
		menu: {},
		menuHandler: function() {},
		initComponent: function() {
			var that = this;
			this.defaultValue = this.value;
			this.menu = Ext.create('Ext.menu.Menu', {
				showSeparator: false,
				items: {
					xtype: 'colorpicker',
					closeAction: 'hide',
					listeners: {
						select: function(cp, color) {
							that.setValue(color);
							that.menu.hide();
							that.menuHandler(cp, color);
						}
					}
				}
			});
			this.callParent();
		},
		listeners: {
			render: function() {
				this.setValue(this.value);
			}
		}
	});

	/* MultiSelect */
	Ext.define("Ext.ux.layout.component.form.MultiSelect",{extend:"Ext.layout.component.field.Field",alias:["layout.multiselectfield"],type:"multiselectfield",defaultHeight:200,sizeBodyContents:function(a,b){var c=this;if(!Ext.isNumber(b)){b=c.defaultHeight}c.owner.panel.setSize(a,b)}});

	Ext.define('Ext.ux.form.MultiSelect', {
		extend: 'Ext.form.field.Base',
		alternateClassName: 'Ext.ux.Multiselect',
		alias: ['widget.multiselect', 'widget.multiselectfield'],
		uses: ['Ext.view.BoundList', 'Ext.form.FieldSet', 'Ext.ux.layout.component.form.MultiSelect', 'Ext.view.DragZone', 'Ext.view.DropZone'],
		/**
		 * @cfg {String} listTitle An optional title to be displayed at the top of the selection list.
		 */
		/**
		 * @cfg {String/Array} dragGroup The ddgroup name(s) for the MultiSelect DragZone (defaults to undefined).
		 */
		/**
		 * @cfg {String/Array} dropGroup The ddgroup name(s) for the MultiSelect DropZone (defaults to undefined).
		 */
		/**
		 * @cfg {Boolean} ddReorder Whether the items in the MultiSelect list are drag/drop reorderable (defaults to false).
		 */
		ddReorder: false,
		/**
		 * @cfg {Object/Array} tbar An optional toolbar to be inserted at the top of the control's selection list.
		 * This can be a {@link Ext.toolbar.Toolbar} object, a toolbar config, or an array of buttons/button configs
		 * to be added to the toolbar. See {@link Ext.panel.Panel#tbar}.
		 */
		/**
		 * @cfg {String} appendOnly True if the list should only allow append drops when drag/drop is enabled
		 * (use for lists which are sorted, defaults to false).
		 */
		appendOnly: false,
		/**
		 * @cfg {String} displayField Name of the desired display field in the dataset (defaults to 'text').
		 */
		displayField: 'text',
		/**
		 * @cfg {String} valueField Name of the desired value field in the dataset (defaults to the
		 * value of {@link #displayField}).
		 */
		/**
		 * @cfg {Boolean} allowBlank False to require at least one item in the list to be selected, true to allow no
		 * selection (defaults to true).
		 */
		allowBlank: true,
		/**
		 * @cfg {Number} minSelections Minimum number of selections allowed (defaults to 0).
		 */
		minSelections: 0,
		/**
		 * @cfg {Number} maxSelections Maximum number of selections allowed (defaults to Number.MAX_VALUE).
		 */
		maxSelections: Number.MAX_VALUE,
		/**
		 * @cfg {String} blankText Default text displayed when the control contains no items (defaults to 'This field is required')
		 */
		blankText: 'This field is required',
		/**
		 * @cfg {String} minSelectionsText Validation message displayed when {@link #minSelections} is not met (defaults to 'Minimum {0}
		 * item(s) required'). The {0} token will be replaced by the value of {@link #minSelections}.
		 */
		minSelectionsText: 'Minimum {0} item(s) required',
		/**
		 * @cfg {String} maxSelectionsText Validation message displayed when {@link #maxSelections} is not met (defaults to 'Maximum {0}
		 * item(s) allowed'). The {0} token will be replaced by the value of {@link #maxSelections}.
		 */
		maxSelectionsText: 'Maximum {0} item(s) allowed',
		/**
		 * @cfg {String} delimiter The string used to delimit the selected values when {@link #getSubmitValue submitting}
		 * the field as part of a form. Defaults to ','. If you wish to have the selected values submitted as separate
		 * parameters rather than a single delimited parameter, set this to <tt>null</tt>.
		 */
		delimiter: ',',
		/**
		 * @cfg {Ext.data.Store/Array} store The data source to which this MultiSelect is bound (defaults to <tt>undefined</tt>).
		 * Acceptable values for this property are:
		 * <div class="mdetail-params"><ul>
		 * <li><b>any {@link Ext.data.Store Store} subclass</b></li>
		 * <li><b>an Array</b> : Arrays will be converted to a {@link Ext.data.ArrayStore} internally.
		 * <div class="mdetail-params"><ul>
		 * <li><b>1-dimensional array</b> : (e.g., <tt>['Foo','Bar']</tt>)<div class="sub-desc">
		 * A 1-dimensional array will automatically be expanded (each array item will be the combo
		 * {@link #valueField value} and {@link #displayField text})</div></li>
		 * <li><b>2-dimensional array</b> : (e.g., <tt>[['f','Foo'],['b','Bar']]</tt>)<div class="sub-desc">
		 * For a multi-dimensional array, the value in index 0 of each item will be assumed to be the combo
		 * {@link #valueField value}, while the value at index 1 is assumed to be the combo {@link #displayField text}.
		 * </div></li></ul></div></li></ul></div>
		 */
		componentLayout: 'multiselectfield',
		fieldBodyCls: Ext.baseCSSPrefix + 'form-multiselect-body',
		// private
		initComponent: function () {
			var me = this;
			me.bindStore(me.store, true);
			if (me.store.autoCreated) {
				me.valueField = me.displayField = 'field1';
				if (!me.store.expanded) {
					me.displayField = 'field2';
				}
			}
			if (!Ext.isDefined(me.valueField)) {
				me.valueField = me.displayField;
			}
			me.callParent();
		},
		bindStore: function (store, initial) {
			var me = this,
				oldStore = me.store,
				boundList = me.boundList;
			if (oldStore && !initial && oldStore !== store && oldStore.autoDestroy) {
				oldStore.destroy();
			}
			me.store = store ? Ext.data.StoreManager.lookup(store) : null;
			if (boundList) {
				boundList.bindStore(store || null);
			}
		},
		// private
		onRender: function (ct, position) {
			var me = this,
				panel, boundList, selModel;
			me.callParent(arguments);
			boundList = me.boundList = Ext.create('Ext.view.BoundList', {
				multiSelect: true,
				store: me.store,
				displayField: me.displayField,
				border: false
			});
			selModel = boundList.getSelectionModel();
			me.mon(selModel, {
				selectionChange: me.onSelectionChange,
				scope: me
			});
			panel = me.panel = Ext.create('Ext.panel.Panel', {
				title: me.listTitle,
				tbar: me.tbar,
				items: [boundList],
				renderTo: me.bodyEl,
				layout: 'fit'
			});
			// Must set upward link after first render
			panel.ownerCt = me;
			// Set selection to current value
			me.setRawValue(me.rawValue);
		},
		// No content generated via template, it's all added components
		getSubTplMarkup: function () {
			return '';
		},
		// private
		afterRender: function () {
			var me = this;
			me.callParent();
			if (me.ddReorder && !me.dragGroup && !me.dropGroup) {
				me.dragGroup = me.dropGroup = 'MultiselectDD-' + Ext.id();
			}
			if (me.draggable || me.dragGroup) {
				me.dragZone = Ext.create('Ext.view.DragZone', {
					view: me.boundList,
					ddGroup: me.dragGroup,
					dragText: '{0} Item{1}'
				});
			}
			if (me.droppable || me.dropGroup) {
				me.dropZone = Ext.create('Ext.view.DropZone', {
					view: me.boundList,
					ddGroup: me.dropGroup,
					handleNodeDrop: function (data, dropRecord, position) {
						var view = this.view,
							store = view.getStore(),
							records = data.records,
							index;
						// remove the Models from the source Store
						data.view.store.remove(records);
						index = store.indexOf(dropRecord);
						if (position === 'after') {
							index++;
						}
						store.insert(index, records);
						view.getSelectionModel().select(records);
					}
				});
			}
		},
		onSelectionChange: function () {
			this.checkChange();
		},
		/**
		 * Clears any values currently selected.
		 */
		clearValue: function () {
			this.setValue([]);
		},
		/**
		 * Return the value(s) to be submitted for this field. The returned value depends on the {@link #delimiter}
		 * config: If it is set to a String value (like the default ',') then this will return the selected values
		 * joined by the delimiter. If it is set to <tt>null</tt> then the values will be returned as an Array.
		 */
		getSubmitValue: function () {
			var me = this,
				delimiter = me.delimiter,
				val = me.getValue();
			return Ext.isString(delimiter) ? val.join(delimiter) : val;
		},
		// inherit docs
		getRawValue: function () {
			var me = this,
				boundList = me.boundList;
			if (boundList) {
				me.rawValue = Ext.Array.map(boundList.getSelectionModel().getSelection(), function (model) {
					return model.get(me.valueField);
				});
			}
			return me.rawValue;
		},
		// inherit docs
		setRawValue: function (value) {
			var me = this,
				boundList = me.boundList,
				models;
			value = Ext.Array.from(value);
			me.rawValue = value;
			if (boundList) {
				models = [];
				Ext.Array.forEach(value, function (val) {
					var undef, model = me.store.findRecord(me.valueField, val, undef, undef, true, true);
					if (model) {
						models.push(model);
					}
				});
				boundList.getSelectionModel().select(models, false, true);
			}
			return value;
		},
		// no conversion
		valueToRaw: function (value) {
			return value;
		},
		// compare array values
		isEqual: function (v1, v2) {
			var fromArray = Ext.Array.from,
				i, len;
			v1 = fromArray(v1);
			v2 = fromArray(v2);
			len = v1.length;
			if (len !== v2.length) {
				return false;
			}
			for (i = 0; i < len; i++) {
				if (v2[i] !== v1[i]) {
					return false;
				}
			}
			return true;
		},
		getErrors: function (value) {
			var me = this,
				format = Ext.String.format,
				errors = me.callParent(arguments),
				numSelected;
			value = Ext.Array.from(value || me.getValue());
			numSelected = value.length;
			if (!me.allowBlank && numSelected < 1) {
				errors.push(me.blankText);
			}
			if (numSelected < this.minSelections) {
				errors.push(format(me.minSelectionsText, me.minSelections));
			}
			if (numSelected > this.maxSelections) {
				errors.push(format(me.maxSelectionsText, me.maxSelections));
			}
			return errors;
		},
		onDisable: function () {
			this.callParent();
			this.disabled = true;
			this.updateReadOnly();
		},
		onEnable: function () {
			this.callParent();
			this.disabled = false;
			this.updateReadOnly();
		},
		setReadOnly: function (readOnly) {
			this.readOnly = readOnly;
			this.updateReadOnly();
		},
		/**
		 * @private Lock or unlock the BoundList's selection model to match the current disabled/readonly state
		 */
		updateReadOnly: function () {
			var me = this,
				boundList = me.boundList,
				readOnly = me.readOnly || me.disabled;
			if (boundList) {
				boundList.getSelectionModel().setLocked(readOnly);
			}
		},
		onDestroy: function () {
			Ext.destroyMembers(this, 'panel', 'boundList', 'dragZone', 'dropZone');
			this.callParent();
		}
	});


	/*----------------------------------------------------------------------------
	 * OpenStreetMap
	 *--------------------------------------------------------------------------*/

	OpenLayers.Util.OSM={};OpenLayers.Util.OSM.MISSING_TILE_URL="http://www.openstreetmap.org/openlayers/img/404.png";OpenLayers.Util.OSM.originalOnImageLoadError=OpenLayers.Util.onImageLoadError;OpenLayers.Util.onImageLoadError=function(){if(this.src.match(/^http:\/\/[abc]\.[a-z]+\.openstreetmap\.org\//)){this.src=OpenLayers.Util.OSM.MISSING_TILE_URL}else{if(this.src.match(/^http:\/\/[def]\.tah\.openstreetmap\.org\//)){}else{OpenLayers.Util.OSM.originalOnImageLoadError}}};OpenLayers.Layer.OSM.Mapnik=OpenLayers.Class(OpenLayers.Layer.OSM,{initialize:function(d,c){var b=["http://a.tile.openstreetmap.org/${z}/${x}/${y}.png","http://b.tile.openstreetmap.org/${z}/${x}/${y}.png","http://c.tile.openstreetmap.org/${z}/${x}/${y}.png"];c=OpenLayers.Util.extend({numZoomLevels:19,buffer:0,transitionEffect:"resize"},c);var a=[d,b,c];OpenLayers.Layer.OSM.prototype.initialize.apply(this,a)},CLASS_NAME:"OpenLayers.Layer.OSM.Mapnik"});OpenLayers.Layer.OSM.Osmarender=OpenLayers.Class(OpenLayers.Layer.OSM,{initialize:function(d,c){var b=["http://a.tah.openstreetmap.org/Tiles/tile/${z}/${x}/${y}.png","http://b.tah.openstreetmap.org/Tiles/tile/${z}/${x}/${y}.png","http://c.tah.openstreetmap.org/Tiles/tile/${z}/${x}/${y}.png"];c=OpenLayers.Util.extend({numZoomLevels:18,buffer:0,transitionEffect:"resize"},c);var a=[d,b,c];OpenLayers.Layer.OSM.prototype.initialize.apply(this,a)},CLASS_NAME:"OpenLayers.Layer.OSM.Osmarender"});OpenLayers.Layer.OSM.CycleMap=OpenLayers.Class(OpenLayers.Layer.OSM,{initialize:function(d,c){var b=["http://a.tile.opencyclemap.org/cycle/${z}/${x}/${y}.png","http://b.tile.opencyclemap.org/cycle/${z}/${x}/${y}.png","http://c.tile.opencyclemap.org/cycle/${z}/${x}/${y}.png"];c=OpenLayers.Util.extend({numZoomLevels:19,buffer:0,transitionEffect:"resize"},c);var a=[d,b,c];OpenLayers.Layer.OSM.prototype.initialize.apply(this,a)},CLASS_NAME:"OpenLayers.Layer.OSM.CycleMap"});


	/*----------------------------------------------------------------------------
	 * OpenLayers - Cirle
	 *--------------------------------------------------------------------------*/

	OpenLayers.Control.Circle=OpenLayers.Class(OpenLayers.Control,{feature:null,layer:null,radius:5,origin:null,sides:40,angle:null,snapAngle:null,dragControl:null,initialize:function(a){OpenLayers.Control.prototype.initialize.apply(this,arguments)},activate:function(){var a=OpenLayers.Control.prototype.activate.call(this);if(a){var b={displayInLayerSwitcher:false,calculateInRange:function(){return true}};this.map.addLayer(this.layer)}return a},deactivate:function(){var a=OpenLayers.Control.prototype.deactivate.call(this);if(a){if(this.layer.map!=null){this.layer.destroy(false);if(this.feature){this.feature.destroy()}}this.layer=null;this.feature=null}return a},createGeometry:function(){this.angle=Math.PI*((1/this.sides)-(1/2));if(this.snapAngle){this.angle+=this.snapAngle*(Math.PI/180)}this.feature.geometry=OpenLayers.Geometry.Polygon.createRegularPolygon(this.origin,this.radius,this.sides,this.snapAngle)},modifyGeometry:function(){var f,c,b,a;var d=this.feature.geometry.components[0];if(d.components.length!=(this.sides+1)){this.createGeometry();d=this.feature.geometry.components[0]}for(var e=0;e<this.sides;++e){a=d.components[e];f=this.angle+(e*2*Math.PI/this.sides);a.x=this.origin.x+(this.radius*Math.cos(f));a.y=this.origin.y+(this.radius*Math.sin(f));a.clearBounds()}},updateCircle:function(b,a){this.origin=new OpenLayers.Geometry.Point(b.lon,b.lat);this.radius=a*1;if(!this.feature){this.feature=new OpenLayers.Feature.Vector();this.createGeometry();this.layer.addFeatures([this.feature],{silent:true})}else{this.modifyGeometry()}this.layer.drawFeature(this.feature,this.style)},CLASS_NAME:"Meteorage.Circle"});


	/*----------------------------------------------------------------------------
	 * OpenLayers - newSelectFeature
	 *--------------------------------------------------------------------------*/

	OpenLayers.Control.newSelectFeature=OpenLayers.Class(OpenLayers.Control,{multipleKey:null,toggleKey:null,multiple:false,clickout:true,toggle:false,hover:false,onSelect:function(){},onUnselect:function(){},onHoverSelect:function(){},onHoverUnselect:function(){},onClickSelect:function(){},onClickUnselect:function(){},geometryTypes:null,layer:null,callbacks:null,selectStyle:null,renderIntent:"select",handler:null,initialize:function(layer,options){OpenLayers.Control.prototype.initialize.apply(this,[options]);this.layer=layer;this.callbacks=OpenLayers.Util.extend({click:this.clickFeature,clickout:this.clickoutFeature,over:this.overFeature,out:this.outFeature},this.callbacks);var handlerOptions={geometryTypes:this.geometryTypes};this.handler=new OpenLayers.Handler.Feature(this,layer,this.callbacks,handlerOptions);},unselectAll:function(options){var feature;for(var i=this.layer.selectedFeatures.length-1;i>=0;--i){feature=this.layer.selectedFeatures[i];if(!options||options.except!=feature){this.unselect(feature,"click");}}},clickFeature:function(feature){if((this.onSelect.name!=""||this.onClickSelect.name!="")&&!this.hover){var selected=(OpenLayers.Util.indexOf(this.layer.selectedFeatures,feature)>-1);if(selected){if(this.toggleSelect()){this.unselect(feature);}else if(!this.multipleSelect()){this.unselectAll({except:feature});}}else{if(!this.multipleSelect()){this.unselectAll({except:feature});}}
	this.select(feature,"click");}},multipleSelect:function(){return this.multiple||this.handler.evt[this.multipleKey];},toggleSelect:function(){return this.toggle||this.handler.evt[this.toggleKey];},clickoutFeature:function(feature){if(((this.onClickUnselect.name!=""||this.onHoverSelect.name=="")&&!this.hover)&&this.clickout){this.unselectAll();}},overFeature:function(feature){if((this.onHoverSelect.name!=""||this.hover)&&(OpenLayers.Util.indexOf(this.layer.selectedFeatures,feature)==-1)){this.select(feature,"hover");}},outFeature:function(feature){if(this.onHoverUnselect.name!=""||this.hover){this.unselect(feature,"hover");}},select:function(feature,evt){this.layer.selectedFeatures.push(feature);var selectStyle=this.selectStyle||this.renderIntent;this.layer.drawFeature(feature,selectStyle);this.layer.events.triggerEvent("featureselected",{feature:feature});switch(evt){case"hover":this.onHoverSelect(feature);break;case"click":if(this.onClickSelect.name!=""){this.onClickSelect(feature);}else if(this.onSelect.name!=""){this.onSelect(feature);}
	break;default:this.onSelect(feature);break;}},unselect:function(feature,evt){this.layer.drawFeature(feature,"default");OpenLayers.Util.removeItem(this.layer.selectedFeatures,feature);this.layer.events.triggerEvent("featureunselected",{feature:feature});switch(evt){case"hover":this.onHoverUnselect(feature);break;case"click":if(this.onClickUnselect.name!=""){this.onClickUnselect(feature);}else if(this.onUnselect.name!=""){this.onUnselect(feature);}
	break;default:this.onUnselect(feature);break;}},setMap:function(map){this.handler.setMap(map);OpenLayers.Control.prototype.setMap.apply(this,arguments);},CLASS_NAME:"OpenLayers.Control.newSelectFeature"});


	/*----------------------------------------------------------------------------
	 * GeoExt - custom
	 *--------------------------------------------------------------------------*/

	Ext.define("GeoExt.data.LayerModel",{alternateClassName:"GeoExt.data.LayerRecord",extend:"Ext.data.Model",requires:["Ext.data.proxy.Memory","Ext.data.reader.Json"],alias:"model.gx_layer",statics:{createFromLayer:function(a){return this.proxy.reader.readRecords([a]).records[0]}},fields:["id",{name:"title",type:"string",mapping:"name"},{name:"legendURL",type:"string",mapping:"metadata.legendURL"},{name:"hideTitle",type:"bool",mapping:"metadata.hideTitle"},{name:"hideInLegend",type:"bool",mapping:"metadata.hideInLegend"}],proxy:{type:"memory",reader:{type:"json"}},getLayer:function(){return this.raw}});Ext.define("GeoExt.data.LayerStore",{requires:["GeoExt.data.LayerModel"],extend:"Ext.data.Store",model:"GeoExt.data.LayerModel",statics:{MAP_TO_STORE:1,STORE_TO_MAP:2},map:null,constructor:function(b){var c=this;b=Ext.apply({},b);var d=(GeoExt.MapPanel&&b.map instanceof GeoExt.MapPanel)?b.map.map:b.map;delete b.map;if(b.layers){b.data=b.layers}delete b.layers;var a={initDir:b.initDir};delete b.initDir;c.callParent([b]);if(d){this.bind(d,a)}},bind:function(e,a){var b=this;if(b.map){return}b.map=e;a=Ext.apply({},a);var c=a.initDir;if(a.initDir==undefined){c=GeoExt.data.LayerStore.MAP_TO_STORE|GeoExt.data.LayerStore.STORE_TO_MAP}var d=e.layers.slice(0);if(c&GeoExt.data.LayerStore.STORE_TO_MAP){b.each(function(f){b.map.addLayer(f.getLayer())},b)}if(c&GeoExt.data.LayerStore.MAP_TO_STORE){b.loadRawData(d,true)}e.events.on({changelayer:b.onChangeLayer,addlayer:b.onAddLayer,removelayer:b.onRemoveLayer,scope:b});b.on({load:b.onLoad,clear:b.onClear,add:b.onAdd,remove:b.onRemove,update:b.onUpdate,scope:b});b.data.on({replace:b.onReplace,scope:b});b.fireEvent("bind",b,e)},unbind:function(){var a=this;if(a.map){a.map.events.un({changelayer:a.onChangeLayer,addlayer:a.onAddLayer,removelayer:a.onRemoveLayer,scope:a});a.un("load",a.onLoad,a);a.un("clear",a.onClear,a);a.un("add",a.onAdd,a);a.un("remove",a.onRemove,a);a.data.un("replace",a.onReplace,a);a.map=null}},onChangeLayer:function(b){var e=b.layer;var c=this.findBy(function(f,g){return f.getLayer()===e});if(c>-1){var a=this.getAt(c);if(b.property==="order"){if(!this._adding&&!this._removing){var d=this.map.getLayerIndex(e);if(d!==c){this._removing=true;this.remove(a);delete this._removing;this._adding=true;this.insert(d,[a]);delete this._adding}}}else{if(b.property==="name"){a.set("title",e.name)}else{this.fireEvent("update",this,a,Ext.data.Record.EDIT)}}}},onAddLayer:function(b){var c=this;if(!c._adding){c._adding=true;var a=c.proxy.reader.read(b.layer);c.add(a.records);delete c._adding}},onRemoveLayer:function(a){if(this.map.unloadDestroy){if(!this._removing){var b=a.layer;this._removing=true;this.remove(this.getByLayer(b));delete this._removing}}else{this.unbind()}},onLoad:function(c,b,g){if(g){if(!Ext.isArray(b)){b=[b]}if(!this._addRecords){this._removing=true;for(var e=this.map.layers.length-1;e>=0;e--){this.map.removeLayer(this.map.layers[e])}delete this._removing}var a=b.length;if(a>0){var f=new Array(a);for(var d=0;d<a;d++){f[d]=b[d].getLayer()}this._adding=true;this.map.addLayers(f);delete this._adding}}delete this._addRecords},onClear:function(a){this._removing=true;for(var b=this.map.layers.length-1;b>=0;b--){this.map.removeLayer(this.map.layers[b])}delete this._removing},onAdd:function(b,a,c){if(!this._adding){this._adding=true;var e;for(var d=a.length-1;d>=0;--d){e=a[d].getLayer();this.map.addLayer(e);if(c!==this.map.layers.length-1){this.map.setLayerIndex(e,c)}}delete this._adding}},onRemove:function(b,a,c){if(!this._removing){var d=a.getLayer();if(this.map.getLayer(d.id)!=null){this._removing=true;this.removeMapLayer(a);delete this._removing}}},onUpdate:function(c,a,b){if(b===Ext.data.Record.EDIT){if(a.modified&&a.modified.title){var d=a.getLayer();var e=a.get("title");if(e!==d.name){d.setName(e)}}}},removeMapLayer:function(a){this.map.removeLayer(a.getLayer())},onReplace:function(c,a,b){this.removeMapLayer(a)},getByLayer:function(b){var a=this.findBy(function(c){return c.getLayer()===b});if(a>-1){return this.getAt(a)}},destroy:function(){var a=this;a.unbind();a.callParent()},loadRecords:function(a,b){if(b&&b.addRecords){this._addRecords=true}this.callParent(arguments)}});Ext.define("GeoExt.panel.Map",{extend:"Ext.panel.Panel",requires:["GeoExt.data.LayerStore"],alias:"widget.gx_mappanel",alternateClassName:"GeoExt.MapPanel",statics:{guess:function(){var a=Ext.ComponentQuery.query("gx_mappanel");return((a&&a.length>0)?a[0]:null)}},center:null,zoom:null,extent:null,prettyStateKeys:false,map:null,layers:null,stateEvents:["aftermapmove","afterlayervisibilitychange","afterlayeropacitychange","afterlayerorderchange","afterlayernamechange","afterlayeradd","afterlayerremove"],initComponent:function(){if(!(this.map instanceof OpenLayers.Map)){this.map=new OpenLayers.Map(Ext.applyIf(this.map||{},{allOverlays:true}))}var a=this.layers;if(!a||a instanceof Array){this.layers=Ext.create("GeoExt.data.LayerStore",{layers:a,map:this.map.layers.length>0?this.map:null})}if(Ext.isString(this.center)){this.center=OpenLayers.LonLat.fromString(this.center)}else{if(Ext.isArray(this.center)){this.center=new OpenLayers.LonLat(this.center[0],this.center[1])}}if(Ext.isString(this.extent)){this.extent=OpenLayers.Bounds.fromString(this.extent)}else{if(Ext.isArray(this.extent)){this.extent=OpenLayers.Bounds.fromArray(this.extent)}}this.callParent(arguments);this.on("resize",this.onResize,this);this.on("afterlayout",function(){if(typeof this.map.getViewport==="function"){this.items.each(function(b){if(typeof b.addToMapPanel==="function"){b.getEl().appendTo(this.map.getViewport())}},this)}},this);this.map.events.on({moveend:this.onMoveend,changelayer:this.onChangelayer,addlayer:this.onAddlayer,removelayer:this.onRemovelayer,scope:this})},onMoveend:function(a){this.fireEvent("aftermapmove",this,this.map,a)},onChangelayer:function(b){var a=this.map;if(b.property){if(b.property==="visibility"){this.fireEvent("afterlayervisibilitychange",this,a,b)}else{if(b.property==="order"){this.fireEvent("afterlayerorderchange",this,a,b)}else{if(b.property==="nathis"){this.fireEvent("afterlayernathischange",this,a,b)}else{if(b.property==="opacity"){this.fireEvent("afterlayeropacitychange",this,a,b)}}}}}},onAddlayer:function(){this.fireEvent("afterlayeradd")},onRemovelayer:function(){this.fireEvent("afterlayerremove")},onResize:function(){var a=this.map;if(this.body.dom!==a.div){a.render(this.body.dom);this.layers.bind(a);if(a.layers.length>0){this.setInitialExtent()}else{this.layers.on("add",this.setInitialExtent,this,{single:true})}}else{a.updateSize()}},setInitialExtent:function(){var a=this.map;if(!a.getCenter()){if(this.center||this.zoom){a.setCenter(this.center,this.zoom)}else{if(this.extent instanceof OpenLayers.Bounds){a.zoomToExtent(this.extent,true)}else{a.zoomToMaxExtent()}}}},getState:function(){var c=this,e=c.map,d=c.callParent(arguments)||{},b;if(!e){return}var a=e.getCenter();a&&Ext.applyIf(d,{x:a.lon,y:a.lat,zoom:e.getZoom()});c.layers.each(function(f){b=f.getLayer();layerId=this.prettyStateKeys?f.get("title"):f.get("id");d=c.addPropertyToState(d,"visibility_"+layerId,b.getVisibility());d=c.addPropertyToState(d,"opacity_"+layerId,(b.opacity===null)?1:b.opacity)},c);return d},applyState:function(a){var j=this;map=j.map;j.center=new OpenLayers.LonLat(a.x,a.y);j.zoom=a.zoom;var f,c,g,d,b,h;var e=map.layers;for(f=0,c=e.length;f<c;f++){g=e[f];d=j.prettyStateKeys?g.name:g.id;b=a["visibility_"+d];if(b!==undefined){b=(/^true$/i).test(b);if(g.isBaseLayer){if(b){map.setBaseLayer(g)}}else{g.setVisibility(b)}}h=a["opacity_"+d];if(h!==undefined){g.setOpacity(h)}}},onBeforeAdd:function(a){if(Ext.isFunction(a.addToMapPanel)){a.addToMapPanel(this)}this.callParent(arguments)},beforeDestroy:function(){if(this.map&&this.map.events){this.map.events.un({moveend:this.onMoveend,changelayer:this.onChangelayer,scope:this})}if(!this.initialConfig.map||!(this.initialConfig.map instanceof OpenLayers.Map)){if(this.map&&this.map.destroy){this.map.destroy()}}delete this.map;this.callParent(arguments)}});Ext.define("GeoExt.tree.Column",{extend:"Ext.tree.Column",alias:"widget.gx_treecolumn",initComponent:function(){var b=this;b.callParent();var a=b.renderer;this.renderer=function(i,e,d,h,j,f,c){var g=[a(i,e,d,h,j,f,c)];if(d.get("checkedGroup")){g[0]=g[0].replace(/class="([^-]+)-tree-checkbox([^"]+)?"/,'class="$1-tree-checkbox$2 gx-tree-radio"')}g.push('<div class="gx-tree-component gx-tree-component-off" id="tree-record-'+d.id+'"></div>');if(d.uiProvider&&d.uiProvider instanceof "string"){}return g.join("")}},defaultRenderer:function(a){return a}});Ext.define("GeoExt.tree.View",{extend:"Ext.tree.View",alias:"widget.gx_treeview",initComponent:function(){var a=this;a.on("itemupdate",this.onItem,this);a.on("itemadd",this.onItem,this);a.on("createchild",this.createChild,this);return a.callParent(arguments)},onItem:function(a,c,f,b){var e=this;if(!(a instanceof Array)){a=[a]}for(var d=0;d<a.length;d++){this.onNodeRendered(a[d])}},onNodeRendered:function(c){var b=this;var a=Ext.get("tree-record-"+c.id);if(!a){return}if(c.get("layer")){b.fireEvent("createchild",a,c)}if(c.hasChildNodes()){c.eachChild(function(d){b.onNodeRendered(d)},b)}},createChild:function(b,c){var a=c.get("component");if(a){cmpObj=Ext.ComponentManager.create(a);cmpObj.render(b);b.removeCls("gx-tree-component-off")}}});Ext.define("GeoExt.tree.LayerNode",{extend:"Ext.AbstractPlugin",alias:"plugin.gx_layer",init:function(b){this.target=b;var a=b.get("layer");b.set("checked",a.getVisibility());if(!b.get("checkedGroup")&&a.isBaseLayer){b.set("checkedGroup","gx_baselayer")}b.set("fixedText",!!b.text);b.set("leaf",true);if(!b.get("iconCls")){b.set("iconCls","gx-tree-layer-icon")}b.on("afteredit",this.onAfterEdit,this);a.events.on({visibilitychanged:this.onLayerVisibilityChanged,scope:this})},onAfterEdit:function(c,a){var b=this;if(~Ext.Array.indexOf(a,"checked")){b.onCheckChange()}},onLayerVisibilityChanged:function(){if(!this._visibilityChanging){this.target.set("checked",this.target.get("layer").getVisibility())}},onCheckChange:function(){var c=this.target,b=this.target.get("checked");if(b!=c.get("layer").getVisibility()){c._visibilityChanging=true;var a=c.get("layer");if(b&&a.isBaseLayer&&a.map){a.map.setBaseLayer(a)}else{a.setVisibility(b)}delete c._visibilityChanging}}});Ext.define("GeoExt.tree.LayerLoader",{extend:"Ext.util.Observable",requires:["GeoExt.tree.LayerNode"],store:null,filter:function(a){return a.getLayer().displayInLayerSwitcher===true},baseAttrs:null,load:function(a){if(this.fireEvent("beforeload",this,a)){this.removeStoreHandlers();while(a.firstChild){a.removeChild(a.firstChild)}if(!this.store){this.store=GeoExt.MapPanel.guess().layers}this.store.each(function(b){this.addLayerNode(a,b)},this);this.addStoreHandlers(a);this.fireEvent("load",this,a)}},onStoreAdd:function(b,a,c,f){if(!this._reordering){var g=f.get("container").recordIndexToNodeIndex(c+a.length-1,f);for(var d=0,e=a.length;d<e;++d){this.addLayerNode(f,a[d],g)}}},onStoreRemove:function(a,b){if(!this._reordering){this.removeLayerNode(b,a)}},addLayerNode:function(d,a,b){b=b||0;if(this.filter(a)===true){var c=a.getLayer();var e=this.createNode({plugins:[{ptype:"gx_layer"}],layer:c,text:c.name,listeners:{move:this.onChildMove,scope:this}});if(b!==undefined){d.insertChild(b,e)}else{d.appendChild(e)}d.getChildAt(b).on("move",this.onChildMove,this)}},removeLayerNode:function(b,a){if(this.filter(a)===true){var c=b.findChildBy(function(d){return d.get("layer")==a.getLayer()});if(c){c.un("move",this.onChildMove,this);c.remove()}}},onChildMove:function(c,k,l,h){var i=this,g=i.store.getByLayer(c.get("layer")),b=l.get("container"),f=b.loader;i._reordering=true;if(f instanceof i.self&&i.store===f.store){f._reordering=true;i.store.remove(g);var a;if(l.childNodes.length>1){var j=(h===0)?h+1:h-1;a=i.store.findBy(function(m){return l.childNodes[j].get("layer")===m.getLayer()});if(h===0){a++}}else{if(k.parentNode===l.parentNode){var d=l;do{d=d.previousSibling}while(d&&!(d.get("container") instanceof b.self&&d.lastChild));if(d){a=i.store.findBy(function(m){return d.lastChild.get("layer")===m.getLayer()})}else{var e=l;do{e=e.nextSibling}while(e&&!(e.get("container") instanceof b.self&&e.firstChild));if(e){a=i.store.findBy(function(m){return e.firstChild.get("layer")===m.getLayer()})}a++}}}if(a!==undefined){i.store.insert(a,[g])}else{i.store.insert(oldRecordIndex,[g])}delete f._reordering}delete i._reordering},addStoreHandlers:function(b){if(!this._storeHandlers){this._storeHandlers={add:function(c,e,d){this.onStoreAdd(c,e,d,b)},remove:function(c,d){this.onStoreRemove(d,b)}};for(var a in this._storeHandlers){this.store.on(a,this._storeHandlers[a],this)}}},removeStoreHandlers:function(){if(this._storeHandlers){for(var a in this._storeHandlers){this.store.un(a,this._storeHandlers[a],this)}delete this._storeHandlers}},createNode:function(a){if(this.baseAttrs){Ext.apply(a,this.baseAttrs)}return a},destroy:function(){this.removeStoreHandlers()}});Ext.define("GeoExt.tree.LayerContainer",{extend:"Ext.AbstractPlugin",requires:["GeoExt.tree.LayerLoader"],alias:"plugin.gx_layercontainer",defaultText:"Layers",init:function(c){var b=this;var a=b.loader;b.loader=(a&&a instanceof GeoExt.tree.LayerLoader)?a:new GeoExt.tree.LayerLoader(a);c.set("container",b);if(!c.get("text")){c.set("text",b.defaultText);c.commit()}b.loader.load(c)},recordIndexToNodeIndex:function(c,g){var f=this;var b=f.loader.store;var e=b.getCount();var a=g.childNodes.length;var h=-1;for(var d=e-1;d>=0;--d){if(f.loader.filter(b.getAt(d))===true){++h;if(c===d||h>a-1){break}}}return h}});Ext.define("GeoExt.tree.BaseLayerContainer",{extend:"GeoExt.tree.LayerContainer",alias:"plugin.gx_baselayercontainer",defaultText:"Base Layers",init:function(c){var b=this;var a=b.loader;b.loader=Ext.applyIf(a||{},{baseAttrs:Ext.applyIf((a&&a.baseAttrs)||{},{iconCls:"gx-tree-baselayer-icon",checkedGroup:"baselayer"}),filter:function(d){var e=d.getLayer();return e.displayInLayerSwitcher===true&&e.isBaseLayer===true}});b.callParent(arguments)}});Ext.define("GeoExt.tree.Panel",{extend:"Ext.tree.Panel",alias:"widget.gx_treepanel",requires:["GeoExt.tree.Column","GeoExt.tree.View"],viewType:"gx_treeview",initComponent:function(){var a=this;if(!a.columns){if(a.initialConfig.hideHeaders===undefined){a.hideHeaders=true}a.addCls(Ext.baseCSSPrefix+"autowidth-table");a.columns=[{xtype:"gx_treecolumn",text:"Name",width:Ext.isIE6?null:10000,dataIndex:a.displayField}]}a.callParent()}});



	// GIS CORE

	// ext config
	Ext.Ajax.method = 'GET';

	// gis
	GIS = {
		core: {
			instances: []
		},
		i18n: {},
		isDebug: false,
		//isSessionStorage: 'sessionStorage' in window && window['sessionStorage'] !== null,
		logg: []
	};

	GIS.core.getOLMap = function(gis) {
		var olmap,
			addControl;

		addControl = function(name, fn) {
			var button,
				panel;

			button = new OpenLayers.Control.Button({
				displayClass: 'olControlButton',
				trigger: function() {
					fn.call(gis.olmap);
				}
			});

			panel = new OpenLayers.Control.Panel({
				defaultControl: button
			});

			panel.addControls([button]);

			olmap.addControl(panel);

			panel.div.className += ' ' + name;
			panel.div.childNodes[0].className += ' ' + name + 'Button';
		};

		olmap = new OpenLayers.Map({
			controls: [
				new OpenLayers.Control.Navigation({
					zoomWheelEnabled: false,
					documentDrag: true
				}),
				new OpenLayers.Control.MousePosition({
					prefix: '<span id="mouseposition" class="el-fontsize-10"><span class="text-mouseposition-lonlat">LON </span>',
					separator: '<span class="text-mouseposition-lonlat">,&nbsp;LAT </span>',
					suffix: '<div id="google-logo" name="http://www.google.com/intl/en-US_US/help/terms_maps.html" onclick="window.open(Ext.get(this).dom.attributes.name.nodeValue);"></div></span>'
				}),
				new OpenLayers.Control.Permalink(),
				new OpenLayers.Control.ScaleLine({
					geodesic: true,
					maxWidth: 170,
					minWidth: 100
				})
			],
			displayProjection: new OpenLayers.Projection('EPSG:4326'),
			maxExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508),
			mouseMove: {}, // Track all mouse moves
			relocate: {} // Relocate organisation units
		});

		// Map events
		olmap.events.register('mousemove', null, function(e) {
			gis.olmap.mouseMove.x = e.clientX;
			gis.olmap.mouseMove.y = e.clientY;
		});

		olmap.zoomToVisibleExtent = function() {
			gis.util.map.zoomToVisibleExtent(this);
		};

		olmap.closeAllLayers = function() {
			gis.layer.event.core.reset();
			gis.layer.facility.core.reset();
			gis.layer.boundary.core.reset();
			gis.layer.thematic1.core.reset();
			gis.layer.thematic2.core.reset();
			gis.layer.thematic3.core.reset();
			gis.layer.thematic4.core.reset();
		};

		addControl('zoomIn', olmap.zoomIn);
		addControl('zoomOut', olmap.zoomOut);
		addControl('zoomVisible', olmap.zoomToVisibleExtent);
		addControl('measure', function() {
			GIS.core.MeasureWindow(gis).show();
		});

		return olmap;
	};

	GIS.core.getLayers = function(gis) {
		var layers = {},
			createSelectionHandlers,
			layerNumbers = ['1', '2', '3', '4'];

		if (window.google) {
			layers.googleStreets = new OpenLayers.Layer.Google('Google Streets', {
				numZoomLevels: 20,
				animationEnabled: true,
				layerType: gis.conf.finals.layer.type_base,
				layerOpacity: 1,
				setLayerOpacity: function(number) {
					if (number) {
						this.layerOpacity = parseFloat(number);
					}
					this.setOpacity(this.layerOpacity);
				}
			});
			layers.googleStreets.id = 'googleStreets';

			layers.googleHybrid = new OpenLayers.Layer.Google('Google Hybrid', {
				type: google.maps.MapTypeId.HYBRID,
				numZoomLevels: 20,
				animationEnabled: true,
				layerType: gis.conf.finals.layer.type_base,
				layerOpacity: 1,
				setLayerOpacity: function(number) {
					if (number) {
						this.layerOpacity = parseFloat(number);
					}
					this.setOpacity(this.layerOpacity);
				}
			});
			layers.googleHybrid.id = 'googleHybrid';
		}

		layers.openStreetMap = new OpenLayers.Layer.OSM.Mapnik('OpenStreetMap', {
			layerType: gis.conf.finals.layer.type_base,
			layerOpacity: 1,
			setLayerOpacity: function(number) {
				if (number) {
					this.layerOpacity = parseFloat(number);
				}
				this.setOpacity(this.layerOpacity);
			}
		});
		layers.openStreetMap.id = 'openStreetMap';

		layers.event = GIS.core.VectorLayer(gis, 'event', GIS.i18n.event_layer, {opacity: 0.8});
		layers.event.core = new mapfish.GeoStat.Event(gis.olmap, {
			layer: layers.event,
			gis: gis
		});

		layers.facility = GIS.core.VectorLayer(gis, 'facility', GIS.i18n.facility_layer, {opacity: 1});
		layers.facility.core = new mapfish.GeoStat.Facility(gis.olmap, {
			layer: layers.facility,
			gis: gis
		});

		layers.boundary = GIS.core.VectorLayer(gis, 'boundary', GIS.i18n.boundary_layer, {opacity: 0.8});
		layers.boundary.core = new mapfish.GeoStat.Boundary(gis.olmap, {
			layer: layers.boundary,
			gis: gis
		});

		for (var i = 0, number; i < layerNumbers.length; i++) {
			number = layerNumbers[i];

			layers['thematic' + number] = GIS.core.VectorLayer(gis, 'thematic' + number, GIS.i18n.thematic_layer + ' ' + number, {opacity: 0.8});
			layers['thematic' + number].layerCategory = gis.conf.finals.layer.category_thematic,
			layers['thematic' + number].core = new mapfish.GeoStat['Thematic' + number](gis.olmap, {
				layer: layers['thematic' + number],
				gis: gis
			});
		}

		return layers;
	};

	GIS.core.createSelectHandlers = function(gis, layer) {
		var isRelocate = !!GIS.app ? (gis.init.user.isAdmin ? true : false) : false,
			options = {},
			infrastructuralPeriod,
			defaultHoverSelect,
			defaultHoverUnselect,
			defaultClickSelect,
            selectHandlers,
			dimConf = gis.conf.finals.dimension,
            defaultHoverWindow,
            eventWindow;

		defaultHoverSelect = function fn(feature) {
			if (defaultHoverWindow) {
				defaultHoverWindow.destroy();
			}
			defaultHoverWindow = Ext.create('Ext.window.Window', {
				cls: 'gis-window-widget-feature gis-plugin',
				preventHeader: true,
				shadow: false,
				resizable: false,
				items: {
					html: feature.attributes.label
				}
			});

			defaultHoverWindow.show();

			var eastX = gis.viewport.eastRegion.getPosition()[0],
				centerX = gis.viewport.centerRegion.getPosition()[0],
				centerRegionCenterX = centerX + ((eastX - centerX) / 2),
				centerRegionY = gis.viewport.centerRegion.getPosition()[1] + (GIS.app ? 32 : 0);

			defaultHoverWindow.setPosition(centerRegionCenterX - (defaultHoverWindow.getWidth() / 2), centerRegionY);
		};

		defaultHoverUnselect = function fn(feature) {
			defaultHoverWindow.destroy();
		};

		defaultClickSelect = function fn(feature) {
			var showInfo,
				showRelocate,
				drill,
				menu,
				selectHandlers,
				isPoint = feature.geometry.CLASS_NAME === gis.conf.finals.openLayers.point_classname,
				att = feature.attributes;

			// Relocate
			showRelocate = function() {
				if (gis.olmap.relocate.window) {
					gis.olmap.relocate.window.destroy();
				}

				gis.olmap.relocate.window = Ext.create('Ext.window.Window', {
					title: 'Relocate facility',
					layout: 'fit',
					iconCls: 'gis-window-title-icon-relocate',
					cls: 'gis-container-default',
					setMinWidth: function(minWidth) {
						this.setWidth(this.getWidth() < minWidth ? minWidth : this.getWidth());
					},
					items: {
						html: att.name,
						cls: 'gis-container-inner'
					},
					bbar: [
						'->',
						{
							xtype: 'button',
							hideLabel: true,
							text: GIS.i18n.cancel,
							handler: function() {
								gis.olmap.relocate.active = false;
								gis.olmap.relocate.window.destroy();
								gis.olmap.getViewport().style.cursor = 'auto';
							}
						}
					],
					listeners: {
						close: function() {
							gis.olmap.relocate.active = false;
							gis.olmap.getViewport().style.cursor = 'auto';
						}
					}
				});

				gis.olmap.relocate.window.show();
				gis.olmap.relocate.window.setMinWidth(220);

				gis.util.gui.window.setPositionTopRight(gis.olmap.relocate.window);
			};

			// Infrastructural data
			showInfo = function() {
				Ext.Ajax.request({
					url: gis.init.contextPath + gis.conf.finals.url.path_module + 'getFacilityInfo.action',
					params: {
						id: att.id
					},
					success: function(r) {
						var ou = Ext.decode(r.responseText);

						if (layer.infrastructuralWindow) {
							layer.infrastructuralWindow.destroy();
						}

						layer.infrastructuralWindow = Ext.create('Ext.window.Window', {
							title: GIS.i18n.information,
							layout: 'column',
							iconCls: 'gis-window-title-icon-information',
							cls: 'gis-container-default',
							width: 460,
							height: 400, //todo
							period: null,
							items: [
								{
									cls: 'gis-container-inner',
									columnWidth: 0.4,
									bodyStyle: 'padding-right:4px',
									items: function() {
										var a = [];

										if (att.name) {
											a.push({html: GIS.i18n.name, cls: 'gis-panel-html-title'}, {html: att.name, cls: 'gis-panel-html'}, {cls: 'gis-panel-html-separator'});
										}

										if (ou.pa) {
											a.push({html: GIS.i18n.parent_unit, cls: 'gis-panel-html-title'}, {html: ou.pa, cls: 'gis-panel-html'}, {cls: 'gis-panel-html-separator'});
										}

										if (ou.ty) {
											a.push({html: GIS.i18n.type, cls: 'gis-panel-html-title'}, {html: ou.ty, cls: 'gis-panel-html'}, {cls: 'gis-panel-html-separator'});
										}

										if (ou.co) {
											a.push({html: GIS.i18n.code, cls: 'gis-panel-html-title'}, {html: ou.co, cls: 'gis-panel-html'}, {cls: 'gis-panel-html-separator'});
										}

										if (ou.ad) {
											a.push({html: GIS.i18n.address, cls: 'gis-panel-html-title'}, {html: ou.ad, cls: 'gis-panel-html'}, {cls: 'gis-panel-html-separator'});
										}

										if (ou.em) {
											a.push({html: GIS.i18n.email, cls: 'gis-panel-html-title'}, {html: ou.em, cls: 'gis-panel-html'}, {cls: 'gis-panel-html-separator'});
										}

										if (ou.pn) {
											a.push({html: GIS.i18n.phone_number, cls: 'gis-panel-html-title'}, {html: ou.pn, cls: 'gis-panel-html'}, {cls: 'gis-panel-html-separator'});
										}

										return a;
									}()
								},
								{
									xtype: 'form',
									cls: 'gis-container-inner gis-form-widget',
									columnWidth: 0.6,
									bodyStyle: 'padding-left:4px',
									items: [
										{
											html: GIS.i18n.infrastructural_data,
											cls: 'gis-panel-html-title'
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											xtype: 'combo',
											fieldLabel: GIS.i18n.period,
											editable: false,
											valueField: 'id',
											displayField: 'name',
											forceSelection: true,
											width: 255, //todo
											labelWidth: 70,
											store: gis.store.infrastructuralPeriodsByType,
											lockPosition: false,
											listeners: {
												select: function() {
													infrastructuralPeriod = this.getValue();

													layer.widget.infrastructuralDataElementValuesStore.load({
														params: {
															periodId: infrastructuralPeriod,
															organisationUnitId: att.internalId
														}
													});
												}
											}
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											xtype: 'grid',
											cls: 'gis-grid',
											height: 300, //todo
											width: 255,
											scroll: 'vertical',
											columns: [
												{
													id: 'dataElementName',
													text: 'Data element',
													dataIndex: 'dataElementName',
													sortable: true,
													width: 195
												},
												{
													id: 'value',
													header: 'Value',
													dataIndex: 'value',
													sortable: true,
													width: 60
												}
											],
											disableSelection: true,
											store: layer.widget.infrastructuralDataElementValuesStore
										}
									]
								}
							],
							listeners: {
								show: function() {
									if (infrastructuralPeriod) {
										this.down('combo').setValue(infrastructuralPeriod);
										infrastructuralDataElementValuesStore.load({
											params: {
												periodId: infrastructuralPeriod,
												organisationUnitId: att.internalId
											}
										});
									}
								}
							}
						});

						layer.infrastructuralWindow.show();
						gis.util.gui.window.setPositionTopRight(layer.infrastructuralWindow);
					}
				});
			};

			// Drill or float
			drill = function(parentId, parentGraph, level) {
				var view = Ext.clone(layer.core.view),
					loader;

				// parent graph map
				view.parentGraphMap = {};
				view.parentGraphMap[parentId] = parentGraph;

				// dimension
				view.rows = [{
					dimension: dimConf.organisationUnit.objectName,
					items: [
						{id: parentId},
						{id: 'LEVEL-' + level}
					]
				}];

				if (view) {
					loader = layer.core.getLoader();
					loader.updateGui = true;
					loader.zoomToVisibleExtent = true;
					loader.hideMask = true;
					loader.load(view);
				}
			};

			// Menu
			var menuItems = [
				Ext.create('Ext.menu.Item', {
					text: 'Float up',
					iconCls: 'gis-menu-item-icon-float',
					cls: 'gis-plugin',
					disabled: !att.hasCoordinatesUp,
					handler: function() {
						drill(att.grandParentId, att.grandParentParentGraph, parseInt(att.level) - 1);
					}
				}),
				Ext.create('Ext.menu.Item', {
					text: 'Drill down',
					iconCls: 'gis-menu-item-icon-drill',
					cls: 'gis-menu-item-first gis-plugin',
					disabled: !att.hasCoordinatesDown,
					handler: function() {
						drill(att.id, att.parentGraph, parseInt(att.level) + 1);
					}
				})
			];

			if (isRelocate && isPoint) {
				menuItems.push({
					xtype: 'menuseparator'
				});

				menuItems.push( Ext.create('Ext.menu.Item', {
					text: GIS.i18n.relocate,
					iconCls: 'gis-menu-item-icon-relocate',
					disabled: !gis.init.user.isAdmin,
					handler: function(item) {
						gis.olmap.relocate.active = true;
						gis.olmap.relocate.feature = feature;
						gis.olmap.getViewport().style.cursor = 'crosshair';
						showRelocate();
					}
				}));

				menuItems.push( Ext.create('Ext.menu.Item', {
                    text: 'Swap lon/lat',
					iconCls: 'gis-menu-item-icon-relocate',
					disabled: !gis.init.user.isAdmin,
					handler: function(item) {
                        var id = feature.attributes.id,
                            geo = Ext.clone(feature.geometry).transform('EPSG:900913', 'EPSG:4326');

                        if (Ext.isNumber(geo.x) && Ext.isNumber(geo.y) && gis.init.user.isAdmin) {
                            Ext.Ajax.request({
                                url: gis.init.contextPath + '/api/organisationUnits/' + id + '.json?links=false',
                                success: function(r) {
                                    var orgUnit = Ext.decode(r.responseText);

                                    orgUnit.coordinates = '[' + geo.y.toFixed(5) + ',' + geo.x.toFixed(5) + ']';

                                    Ext.Ajax.request({
                                        url: gis.init.contextPath + '/api/metaData?preheatCache=false',
                                        method: 'POST',
                                        headers: {'Content-Type': 'application/json'},
                                        params: Ext.encode({organisationUnits: [orgUnit]}),
                                        success: function(r) {
                                            var x = feature.geometry.x,
                                                y = feature.geometry.y;

                                            delete feature.geometry.bounds;
                                            feature.geometry.x = y;
                                            feature.geometry.y = x;

                                            layer.redraw();

                                            console.log(feature.attributes.name + ' relocated to ' + orgUnit.coordinates);
                                        }
                                    });
                                }
                            });
                        }
					}
				}));

				menuItems.push( Ext.create('Ext.menu.Item', {
					text: GIS.i18n.show_information_sheet,
					iconCls: 'gis-menu-item-icon-information',
					handler: function(item) {
						if (gis.store.infrastructuralPeriodsByType.isLoaded) {
							showInfo();
						}
						else {
							gis.store.infrastructuralPeriodsByType.load({
								params: {
									name: gis.init.systemSettings.infrastructuralPeriodType
								},
								callback: function() {
									showInfo();
								}
							});
						}
					}
				}));
			}

			menuItems[menuItems.length - 1].addCls('gis-menu-item-last');

			menu = new Ext.menu.Menu({
				baseCls: 'gis-plugin',
				shadow: false,
				showSeparator: false,
				defaults: {
					bodyStyle: 'padding-right:6px'
				},
				items: menuItems,
				listeners: {
					afterrender: function() {
						this.getEl().addCls('gis-toolbar-btn-menu');
					}
				}
			});

			menu.showAt([gis.olmap.mouseMove.x, gis.olmap.mouseMove.y]);
		};

		options = {
            onHoverSelect: defaultHoverSelect,
            onHoverUnselect: defaultHoverUnselect,
            onClickSelect: defaultClickSelect
        };

		if (layer.id === 'event') {
			options.onClickSelect = function fn(feature) {
                var ignoreKeys = ['label', 'value', 'nameColumnMap', 'psi', 'ps', 'longitude', 'latitude', 'eventdate', 'ou', 'oucode', 'ouname'],
                    attributes = feature.attributes,
                    map = attributes.nameColumnMap,
                    html = '<table class="padding1">',
                    titleStyle = ' style="font-weight:bold; padding-right:10px; vertical-align:top"',
                    valueStyle = ' style="max-width:170px"',
                    windowPosition;

                // default properties
                html += '<tr><td' + titleStyle + '>' + map['ou'] + '</td><td' + valueStyle + '>' + attributes['ouname'] + '</td></tr>';
                html += '<tr><td' + titleStyle + '>' + map['eventdate'] + '</td><td' + valueStyle + '>' + attributes['eventdate'] + '</td></tr>';
                html += '<tr><td' + titleStyle + '>' + map['longitude'] + '</td><td' + valueStyle + '>' + attributes['longitude'] + '</td></tr>';
                html += '<tr><td' + titleStyle + '>' + map['latitude'] + '</td><td' + valueStyle + '>' + attributes['latitude'] + '</td></tr>';

                for (var key in attributes) {
                    if (attributes.hasOwnProperty(key) && !Ext.Array.contains(ignoreKeys, key)) {
                        html += '<tr><td' + titleStyle + '>' + map[key] + '</td><td>' + attributes[key] + '</td></tr>';
                    }
                }

                html += '</table>';

                if (Ext.isObject(eventWindow) && eventWindow.destroy) {
                    windowPosition = eventWindow.getPosition();
                    eventWindow.destroy();
                    eventWindow = null;
                }

                eventWindow = Ext.create('Ext.window.Window', {
                    title: 'Event',
                    layout: 'fit',
                    resizable: false,
                    bodyStyle: 'background-color:#fff; padding:5px',
                    html: html,
                    autoShow: true,
                    listeners: {
                        show: function(w) {
                            if (windowPosition) {
                                w.setPosition(windowPosition);
                            }
                            else {
                                gis.util.gui.window.setPositionTopRight(w);
                            }
                        },
                        destroy: function() {
                            eventWindow = null;
                        }
                    }
                });
            };
		}

		selectHandlers = new OpenLayers.Control.newSelectFeature(layer, options);

		gis.olmap.addControl(selectHandlers);
		selectHandlers.activate();
	};

	GIS.core.OrganisationUnitLevelStore = function(gis) {
		return Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'level'],
			proxy: {
				type: 'jsonp',
				url: gis.init.contextPath + gis.conf.finals.url.path_api + 'organisationUnitLevels.jsonp?viewClass=detailed&links=false&paging=false',
				reader: {
					type: 'json',
					root: 'organisationUnitLevels'
				}
			},
			autoLoad: true,
			cmp: [],
			isLoaded: false,
			loadFn: function(fn) {
				if (this.isLoaded) {
					fn.call();
				}
				else {
					this.load(fn);
				}
			},
			getRecordByLevel: function(level) {
				return this.getAt(this.findExact('level', level));
			},
			listeners: {
				load: function() {
					if (!this.isLoaded) {
						this.isLoaded = true;
						gis.util.gui.combo.setQueryMode(this.cmp, 'local');
					}
					this.sort('level', 'ASC');
				}
			}
		});
	};

	GIS.core.StyleMap = function(id, labelConfig) {
		var defaults = {
				fillOpacity: 1,
				strokeColor: '#fff',
				strokeWidth: 1
			},
			select = {
				fillOpacity: 0.9,
				strokeColor: '#fff',
				strokeWidth: 1,
				cursor: 'pointer'
			};

		if (id === 'boundary') {
			defaults.fillOpacity = 0;
			defaults.strokeColor = '#000';
			defaults.strokeWidth = 1;

			select.fillColor = '#000';
			select.fillOpacity = 0.15;
			select.strokeColor = '#000';
			select.strokeWidth = 1;
		}

		if (labelConfig) {
			defaults.label = '\${label}';
			defaults.fontFamily = 'arial,sans-serif,ubuntu,consolas';
			defaults.fontSize = labelConfig.fontSize ? labelConfig.fontSize + 'px' : '13px';
			defaults.fontWeight = labelConfig.strong ? 'bold' : 'normal';
			defaults.fontStyle = labelConfig.italic ? 'italic' : 'normal';
			defaults.fontColor = labelConfig.color ? (labelConfig.color.split('').shift() !== '#' ? '#' + labelConfig.color : labelConfig.color) : '#000000';
		}

		return new OpenLayers.StyleMap({
			'default': defaults,
			select: select
		});
	};

	GIS.core.VectorLayer = function(gis, id, name, config) {
		var layer = new OpenLayers.Layer.Vector(name, {
			strategies: [
				new OpenLayers.Strategy.Refresh({
					force:true
				})
			],
			styleMap: GIS.core.StyleMap(id),
			visibility: false,
			displayInLayerSwitcher: false,
			layerType: gis.conf.finals.layer.type_vector,
			layerOpacity: config ? config.opacity || 1 : 1,
			setLayerOpacity: function(number) {
				if (number) {
					this.layerOpacity = parseFloat(number);
				}
				this.setOpacity(this.layerOpacity);
			},
			hasLabels: false
		});

		layer.id = id;

		return layer;
	};

	GIS.core.MeasureWindow = function(gis) {
		var window,
			label,
			handleMeasurements,
			control,
			styleMap;

		styleMap = new OpenLayers.StyleMap({
			'default': new OpenLayers.Style()
		});

		control = new OpenLayers.Control.Measure(OpenLayers.Handler.Path, {
			persist: true,
			immediate: true,
			handlerOption: {
				layerOptions: {
					styleMap: styleMap
				}
			}
		});

		handleMeasurements = function(e) {
			if (e.measure) {
				label.setText(e.measure.toFixed(2) + ' ' + e.units);
			}
		};

		gis.olmap.addControl(control);

		control.events.on({
			measurepartial: handleMeasurements,
			measure: handleMeasurements
		});

		control.geodesic = true;
		control.activate();

		label = Ext.create('Ext.form.Label', {
			style: 'height: 20px',
			text: '0 km'
		});

		window = Ext.create('Ext.window.Window', {
			title: GIS.i18n.measure_distance,
			layout: 'fit',
			cls: 'gis-container-default gis-plugin',
			bodyStyle: 'text-align: center',
			width: 130,
			minWidth: 130,
			resizable: false,
			items: label,
			listeners: {
				show: function() {
					var x = gis.viewport.eastRegion.getPosition()[0] - this.getWidth() - 3,
						y = gis.viewport.centerRegion.getPosition()[1] + 26;
					this.setPosition(x, y);
				},
				destroy: function() {
					control.deactivate();
					gis.olmap.removeControl(control);
				}
			}
		});

		return window;
	};

	GIS.core.MapLoader = function(gis) {
		var getMap,
			setMap,
			afterLoad,
			callBack,
			register = [],
			loader;

		getMap = function() {
			Ext.data.JsonP.request({
				url: gis.init.contextPath + gis.conf.finals.url.path_api + 'maps/' + gis.map.id + '.jsonp?viewClass=dimensional&links=false',
				success: function(r) {

					// Operand
					if (Ext.isArray(r.mapViews)) {
						for (var i = 0, view; i < r.mapViews.length; i++) {
							view = r.mapViews[i];

							if (view) {
								if (Ext.isArray(view.columns) && view.columns.length) {
									for (var j = 0, dim; j < view.columns.length; j++) {
										dim = view.columns[j];

										if (Ext.isArray(dim.items) && dim.items.length) {
											for (var k = 0, item; k < dim.items.length; k++) {
												item = dim.items[k];

												item.id = item.id.replace('.', '-');
											}
										}
									}
								}
							}
						}
					}

					gis.map = r;
					setMap();
				},
				failure: function() {
					gis.olmap.mask.hide();
					alert('Map id not recognized' + (gis.el ? ' (' + gis.el + ')' : ''));
					return;
				}
			});
		};

		setMap = function() {
			var views = gis.map.mapViews,
				loader;

			if (!(Ext.isArray(views) && views.length)) {
				gis.olmap.mask.hide();
				alert(GIS.i18n.favorite_outdated_create_new);
				return;
			}

			for (var i = 0; i < views.length; i++) {
				views[i] = gis.api.layout.Layout(views[i]);
			}

			views = Ext.Array.clean(views);

			if (!views.length) {
				return;
			}

			if (gis.viewport && gis.viewport.favoriteWindow && gis.viewport.favoriteWindow.isVisible()) {
				gis.viewport.favoriteWindow.destroy();
			}

			gis.olmap.closeAllLayers();

			for (var i = 0, layout; i < views.length; i++) {
				layout = views[i];

				loader = gis.layer[layout.layer].core.getLoader();
				loader.updateGui = !gis.el;
				loader.callBack = callBack;
				loader.load(layout);
			}
		};

		callBack = function(layer) {
			register.push(layer);

			if (register.length === gis.map.mapViews.length) {
				afterLoad();
			}
		};

		afterLoad = function() {
			register = [];

			if (gis.el) {
				gis.olmap.zoomToVisibleExtent();
			}
			else {
				if (gis.map.longitude && gis.map.latitude && gis.map.zoom) {
					gis.olmap.setCenter(new OpenLayers.LonLat(gis.map.longitude, gis.map.latitude), gis.map.zoom);
				}
				else {
					gis.olmap.zoomToVisibleExtent();
				}
			}

			// interpretation button
			if (gis.map.id && gis.viewport.shareButton) {
				gis.viewport.shareButton.enable();
			}

			// session storage
			if (GIS.isSessionStorage) {
				gis.util.layout.setSessionStorage('map', gis.util.layout.getAnalytical());
			}

			gis.olmap.mask.hide();
		};

		loader = {
			load: function(views) {
				gis.olmap.mask.show();

				if (gis.map && gis.map.id) {
					getMap();
				}
				else {
					if (views) {
						gis.map = {
							mapViews: views
						};
					}

					setMap();
				}
			}
		};

		return loader;
	};

	GIS.core.LayerLoaderEvent = function(gis, layer) {
		var olmap = layer.map,
			compareView,
			loadOrganisationUnits,
			loadData,
			loadLegend,
			afterLoad,
			loader,
			dimConf = gis.conf.finals.dimension;

		loadOrganisationUnits = function(view) {
            loadData(view);
		};

		loadData = function(view) {
			view = view || layer.core.view;

            var paramString = '?',
                features = [];

            // stage
            paramString += 'stage=' + view.stage.id;

            // dates
            paramString += '&startDate=' + view.startDate;
            paramString += '&endDate=' + view.endDate;

            // ou
            if (Ext.isArray(view.organisationUnits)) {
				for (var i = 0; i < view.organisationUnits.length; i++) {
					paramString += '&dimension=ou:' + view.organisationUnits[i].id;
				}
			}

            // de
            for (var i = 0, element; i < view.dataElements.length; i++) {
                element = view.dataElements[i];

                paramString += '&dimension=' + element.id;

                if (element.value) {
					if (element.operator) {
						paramString += ':' + element.operator;
					}

					paramString += ':' + element.value;
                }
            }

			Ext.data.JsonP.request({
				url: gis.init.contextPath + '/api/analytics/events/query/' + view.program.id + '.jsonp' + paramString,
				disableCaching: false,
				scope: this,
				success: function(r) {
                    var events = [],
                        features = [],
                        rows = [],
                        lonIndex,
                        latIndex,
                        map = Ext.clone(r.metaData.names);

                    // name-column map, lonIndex, latIndex
                    for (var i = 0; i < r.headers.length; i++) {
                        map[r.headers[i].name] = r.headers[i].column;

                        if (r.headers[i].name === 'longitude') {
							lonIndex = i;
						}

						if (r.headers[i].name === 'latitude') {
							latIndex = i;
						}
                    }

					// get events with coordinates
                    if (Ext.isArray(r.rows) && r.rows.length) {
						for (var i = 0, row; i < r.rows.length; i++) {
							row = r.rows[i];

							if (row[lonIndex] && row[latIndex]) {
								rows.push(row);
							}
						}
					}

                    if (!rows.length) {
                        alert('No coordinates found');
                        olmap.mask.hide();
                        return;
                    }

                    // name-column map
                    map = Ext.clone(r.metaData.names);

                    for (var i = 0; i < r.headers.length; i++) {
                        map[r.headers[i].name] = r.headers[i].column;
                    }

                    // events
                    for (var i = 0, row, obj; i < rows.length; i++) {
                        row = rows[i];						
                        obj = {};

                        for (var j = 0; j < row.length; j++) {
                            obj[r.headers[j].name] = row[j];
                        }

                        obj[gis.conf.finals.widget.value] = 0;
                        obj.label = obj.ouname;
                        obj.nameColumnMap = map;

                        events.push(obj);
                    }

                    // features
                    for (var i = 0, event, point; i < events.length; i++) {
                        event = events[i];

                        point = gis.util.map.getTransformedPointByXY(event.longitude, event.latitude);

                        features.push(new OpenLayers.Feature.Vector(point, event));
                    }

                    layer.removeFeatures(layer.features);
                    layer.addFeatures(features);

                    loadLegend(view);
                }
            });
		};

		loadLegend = function(view) {
			view = view || layer.core.view;

            // classification optionsvar options = {
            var options = {
            	indicator: gis.conf.finals.widget.value,
				method: 2,
				numClasses: 5,
				colors: layer.core.getColors('000000', '222222'),
				minSize: 5,
				maxSize: 5
			};

            layer.core.view = view;

            layer.core.applyClassification(options);

            afterLoad(view);
		};

		afterLoad = function(view) {

			// Layer
			if (layer.item) {
				layer.item.setValue(true, view.opacity);
			}
			else {
				layer.setLayerOpacity(view.opacity);
			}

			// Gui
			if (loader.updateGui && Ext.isObject(layer.widget)) {
				layer.widget.setGui(view);
			}

			// Zoom
			if (loader.zoomToVisibleExtent) {
				olmap.zoomToVisibleExtent();
			}

			// Mask
			if (loader.hideMask) {
				olmap.mask.hide();
			}

			// Map callback
			if (loader.callBack) {
				loader.callBack(layer);
			}
			else {
				gis.map = null;
			}

			// session storage
			//if (GIS.isSessionStorage) {
				//gis.util.layout.setSessionStorage('map', gis.util.layout.getAnalytical());
			//}
		};

		loader = {
			compare: false,
			updateGui: false,
			zoomToVisibleExtent: false,
			hideMask: false,
			callBack: null,
			load: function(view) {
				gis.olmap.mask.show();

                loadOrganisationUnits(view);
			},
			loadData: loadData,
			loadLegend: loadLegend
		};

		return loader;
	};

	GIS.core.LayerLoaderFacility = function(gis, layer) {
		var olmap = layer.map,
			compareView,
			loadOrganisationUnits,
			loadData,
			loadLegend,
			addCircles,
			afterLoad,
			loader;

		compareView = function(view, doExecute) {
			var src = layer.core.view,
				viewIds,
				viewDim,
				srcIds,
				srcDim;

			loader.zoomToVisibleExtent = true;

			if (!src) {
				if (doExecute) {
					loadOrganisationUnits(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}

			// organisation units
			viewIds = [];
			viewDim = view.rows[0];
			srcIds = [];
			srcDim = src.rows[0];

			if (viewDim.items.length === srcDim.items.length) {
				for (var i = 0; i < viewDim.items.length; i++) {
					viewIds.push(viewDim.items[i].id);
				}

				for (var i = 0; i < srcDim.items.length; i++) {
					srcIds.push(srcDim.items[i].id);
				}

				if (Ext.Array.difference(viewIds, srcIds).length !== 0) {
					if (doExecute) {
						loadOrganisationUnits(view);
					}
					return gis.conf.finals.widget.loadtype_organisationunit;
				}
			}
			else {
				if (doExecute) {
					loadOrganisationUnits(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}

			// Group set
			loader.zoomToVisibleExtent = false;

			if (view.organisationUnitGroupSet.id !== src.organisationUnitGroupSet.id) {
				if (doExecute) {
					loadOrganisationUnits(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}

			//if (view.areaRadius !== src.areaRadius) {
				//if (doExecute) {
					//loadLegend(view);
				//}
				//return gis.conf.finals.widget.loadtype_legend;
			//}

			// always reload legend
			if (doExecute) {
				loadLegend(view);
				return gis.conf.finals.widget.loadtype_legend;
			}

			//gis.olmap.mask.hide();
		};

		loadOrganisationUnits = function(view) {
			var items = view.rows[0].items,
				idParamString = '';

			for (var i = 0; i < items.length; i++) {
				idParamString += 'ids=' + items[i].id;
				idParamString += i !== items.length - 1 ? '&' : '';
			}

			Ext.data.JsonP.request({
				url: gis.init.contextPath + gis.conf.finals.url.path_module + 'getGeoJsonFacilities.action?' + idParamString,
				scope: this,
				disableCaching: false,
				success: function(r) {
					var geojson = layer.core.decode(r),
						format = new OpenLayers.Format.GeoJSON(),
						features = gis.util.map.getTransformedFeatureArray(format.read(geojson));

					if (!Ext.isArray(features)) {
						olmap.mask.hide();
						alert(GIS.i18n.invalid_coordinates);
						return;
					}

					if (!features.length) {
						olmap.mask.hide();
						alert(GIS.i18n.no_valid_coordinates_found);
						return;
					}

					layer.core.featureStore.loadFeatures(features.slice(0));

					loadData(view, features);
				},
				failure: function(r) {
					olmap.mask.hide();
					alert(GIS.i18n.coordinates_could_not_be_loaded);
				}
			});
		};

		loadData = function(view, features) {
			view = view || layer.core.view;
			features = features || layer.core.featureStore.features;

			for (var i = 0; i < features.length; i++) {
				features[i].attributes.label = features[i].attributes.name;
			}

			layer.removeFeatures(layer.features);
			layer.addFeatures(features);

			loadLegend(view);
		};

		loadLegend = function(view) {
			view = view || layer.core.view;

			var store = gis.store.groupsByGroupSet;

			store.proxy.url = gis.init.contextPath + gis.conf.finals.url.path_module + 'getOrganisationUnitGroupsByGroupSet.action?id=' + view.organisationUnitGroupSet.id;
			store.load({
				scope: this,
				callback: function() {
					var options = {
						indicator: view.organisationUnitGroupSet.id
					};

					layer.core.view = view;

					layer.core.applyClassification(options);

					addCircles(view);

					afterLoad(view);
				}
			});
		};

		addCircles = function(view) {
			var radius = view.areaRadius;

			if (layer.circleLayer) {
				layer.circleLayer.deactivateControls();
				layer.circleLayer = null;
			}
			if (Ext.isDefined(radius) && radius) {
				layer.circleLayer = GIS.app.CircleLayer(layer.features, radius);
				nissa = layer.circleLayer;
			}
		};

		afterLoad = function(view) {

			// Legend
			gis.viewport.eastRegion.doLayout();
			layer.legendPanel.expand();

			// Layer
			if (layer.item) {
				layer.item.setValue(true, view.opacity);
			}
			else {
				layer.setLayerOpacity(view.opacity);
			}

			// Gui
			if (loader.updateGui && Ext.isObject(layer.widget)) {
				layer.widget.setGui(view);
			}

			// Zoom
			if (loader.zoomToVisibleExtent) {
				olmap.zoomToVisibleExtent();
			}

			// Mask
			if (loader.hideMask) {
				olmap.mask.hide();
			}

			// Map callback
			if (loader.callBack) {
				loader.callBack(layer);
			}
			else {
				gis.map = null;
			}
		};

		loader = {
			compare: false,
			updateGui: false,
			zoomToVisibleExtent: false,
			hideMask: false,
			callBack: null,
			load: function(view) {
				gis.olmap.mask.show();

				if (this.compare) {
					compareView(view, true);
				}
				else {
					loadOrganisationUnits(view);
				}
			},
			loadData: loadData,
			loadLegend: loadLegend
		};

		return loader;
	};

	GIS.core.LayerLoaderBoundary = function(gis, layer) {
		var olmap = layer.map,
			compareView,
			loadOrganisationUnits,
			loadData,
			loadLegend,
			afterLoad,
			loader;

		compareView = function(view, doExecute) {
			var src = layer.core.view,
				viewIds,
				viewDim,
				srcIds,
				srcDim;

			if (!src) {
				if (doExecute) {
					loadOrganisationUnits(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}

			viewIds = [];
			viewDim = view.rows[0];
			srcIds = [];
			srcDim = src.rows[0];

			// organisation units
			if (viewDim.items.length === srcDim.items.length) {
				for (var i = 0; i < viewDim.items.length; i++) {
					viewIds.push(viewDim.items[i].id);
				}

				for (var i = 0; i < srcDim.items.length; i++) {
					srcIds.push(srcDim.items[i].id);
				}

				if (Ext.Array.difference(viewIds, srcIds).length !== 0) {
					if (doExecute) {
						loadOrganisationUnits(view);
					}
					return gis.conf.finals.widget.loadtype_organisationunit;
				}
			}
			else {
				if (doExecute) {
					loadOrganisationUnits(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}

			gis.olmap.mask.hide();
		};

		loadOrganisationUnits = function(view) {
			var items = view.rows[0].items,
				idParamString = '';

			for (var i = 0; i < items.length; i++) {
				idParamString += 'ids=' + items[i].id;
				idParamString += i !== items.length - 1 ? '&' : '';
			}

			Ext.data.JsonP.request({
				url: gis.init.contextPath + gis.conf.finals.url.path_module + 'getGeoJson.action?' + idParamString,
				scope: this,
				disableCaching: false,
				success: function(r) {
					var geojson = gis.util.geojson.decode(r),
						format = new OpenLayers.Format.GeoJSON(),
						features = gis.util.map.getTransformedFeatureArray(format.read(geojson));

					if (!Ext.isArray(features)) {
						olmap.mask.hide();
						alert(GIS.i18n.invalid_coordinates);
						return;
					}

					if (!features.length) {
						olmap.mask.hide();
						alert(GIS.i18n.no_valid_coordinates_found);
						return;
					}

					layer.core.featureStore.loadFeatures(features.slice(0));

					loadData(view, features);
				},
				failure: function(r) {
					olmap.mask.hide();
					alert(GIS.i18n.coordinates_could_not_be_loaded);
				}
			});
		};

		loadData = function(view, features) {
			view = view || layer.core.view;
			features = features || layer.core.featureStore.features;

			for (var i = 0; i < features.length; i++) {
				features[i].attributes.label = features[i].attributes.name;
				features[i].attributes.value = 0;
			}

			layer.removeFeatures(layer.features);
			layer.addFeatures(features);

			loadLegend(view);
		};

		loadLegend = function(view) {
			view = view || layer.core.view;

			var options = {
				indicator: gis.conf.finals.widget.value,
				method: 2,
				numClasses: 5,
				colors: layer.core.getColors('000000', '000000'),
				minSize: 6,
				maxSize: 6
			};

			// Labels
			if (view.labels) {
				if (Ext.isObject(view.labels)) {
					layer.styleMap = GIS.core.StyleMap(layer.id, view.labels);
				}
				else {
					layer.styleMap = GIS.core.StyleMap(layer.id, {
						fontSize: 12,
						strong: false,
						italic: false,
						color: '#000'
					});
				}
			}

			layer.core.view = view;

			layer.core.applyClassification(options);

			afterLoad(view);
		};

		afterLoad = function(view) {

			// Layer
			if (layer.item) {
				layer.item.setValue(true, view.opacity);
			}
			else {
				layer.setLayerOpacity(view.opacity);
			}

			// Gui
			if (loader.updateGui && Ext.isObject(layer.widget)) {
				layer.widget.setGui(view);
			}

			// Zoom
			if (loader.zoomToVisibleExtent) {
				olmap.zoomToVisibleExtent();
			}

			// Mask
			if (loader.hideMask) {
				olmap.mask.hide();
			}

			// Map callback
			if (loader.callBack) {
				loader.callBack(layer);
			}
			else {
				gis.map = null;
			}
		};

		loader = {
			compare: false,
			updateGui: false,
			zoomToVisibleExtent: false,
			hideMask: false,
			callBack: null,
			load: function(view) {
				gis.olmap.mask.show();

				if (this.compare) {
					compareView(view, true);
				}
				else {
					loadOrganisationUnits(view);
				}
			},
			loadData: loadData,
			loadLegend: loadLegend
		};

		return loader;
	};

	GIS.core.LayerLoaderThematic = function(gis, layer) {
		var olmap = layer.map,
			compareView,
			loadOrganisationUnits,
			loadData,
			loadLegend,
			afterLoad,
			loader,
			dimConf = gis.conf.finals.dimension;

		compareView = function(view, doExecute) {
			var src = layer.core.view,
				viewIds,
				viewDim,
				srcIds,
				srcDim;

			loader.zoomToVisibleExtent = true;

			if (!src) {
				if (doExecute) {
					loadOrganisationUnits(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}

			// organisation units
			viewIds = [];
			viewDim = view.rows[0];
			srcIds = [];
			srcDim = src.rows[0];

			if (viewDim.items.length === srcDim.items.length) {
				for (var i = 0; i < viewDim.items.length; i++) {
					viewIds.push(viewDim.items[i].id);
				}

				for (var i = 0; i < srcDim.items.length; i++) {
					srcIds.push(srcDim.items[i].id);
				}

				if (Ext.Array.difference(viewIds, srcIds).length !== 0) {
					if (doExecute) {
						loadOrganisationUnits(view);
					}
					return gis.conf.finals.widget.loadtype_organisationunit;
				}
			}
			else {
				if (doExecute) {
					loadOrganisationUnits(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}

			// data
			loader.zoomToVisibleExtent = false;

			viewIds = [];
			viewDim = view.columns[0];
			srcIds = [];
			srcDim = src.columns[0];

			if (viewDim.items.length === srcDim.items.length) {
				for (var i = 0; i < viewDim.items.length; i++) {
					viewIds.push(viewDim.items[i].id);
				}

				for (var i = 0; i < srcDim.items.length; i++) {
					srcIds.push(srcDim.items[i].id);
				}

				if (Ext.Array.difference(viewIds, srcIds).length !== 0) {
					if (doExecute) {
						loadData(view);
					}
					return gis.conf.finals.widget.loadtype_organisationunit;
				}
			}
			else {
				if (doExecute) {
					loadData(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}

			// period
			viewIds = [];
			viewDim = view.filters[0];
			srcIds = [];
			srcDim = src.filters[0];

			if (viewDim.items.length === srcDim.items.length) {
				for (var i = 0; i < viewDim.items.length; i++) {
					viewIds.push(viewDim.items[i].id);
				}

				for (var i = 0; i < srcDim.items.length; i++) {
					srcIds.push(srcDim.items[i].id);
				}

				if (Ext.Array.difference(viewIds, srcIds).length !== 0) {
					if (doExecute) {
						loadData(view);
					}
					return gis.conf.finals.widget.loadtype_organisationunit;
				}
			}
			else {
				if (doExecute) {
					loadData(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}

			// legend
			//if (typeof view.legendSet !== typeof src.legendSet) {
				//if (doExecute) {
					//loadLegend(view);
				//}
				//return gis.conf.finals.widget.loadtype_legend;
			//}
			//else if (view.classes !== src.classes ||
				//view.method !== src.method ||
				//view.colorLow !== src.colorLow ||
				//view.radiusLow !== src.radiusLow ||
				//view.colorHigh !== src.colorHigh ||
				//view.radiusHigh !== src.radiusHigh) {
					//if (doExecute) {
						//loadLegend(view);
					//}
					//return gis.conf.finals.widget.loadtype_legend;
			//}

			// if no changes - reload legend but do not zoom
			if (doExecute) {
				loader.zoomToVisibleExtent = false;
				loadLegend(view);
				return gis.conf.finals.widget.loadtype_legend;
			}

			//gis.olmap.mask.hide();
		};

		loadOrganisationUnits = function(view) {
			var items = view.rows[0].items,
				idParamString = '';

			for (var i = 0; i < items.length; i++) {
				idParamString += 'ids=' + items[i].id;
				idParamString += i !== items.length - 1 ? '&' : '';
			}

			Ext.data.JsonP.request({
				url: gis.init.contextPath + gis.conf.finals.url.path_module + 'getGeoJson.action?' + idParamString,
				scope: this,
				disableCaching: false,
				success: function(r) {
					var geojson = gis.util.geojson.decode(r),
						format = new OpenLayers.Format.GeoJSON(),
						features = gis.util.map.getTransformedFeatureArray(format.read(geojson));

					if (!Ext.isArray(features)) {
						olmap.mask.hide();
						alert(GIS.i18n.invalid_coordinates);
						return;
					}

					if (!features.length) {
						olmap.mask.hide();
						alert(GIS.i18n.no_valid_coordinates_found);
						return;
					}

					layer.core.featureStore.loadFeatures(features.slice(0));

					loadData(view, features);
				},
				failure: function(r) {
					olmap.mask.hide();
					alert(GIS.i18n.coordinates_could_not_be_loaded);
				}
			});
		};

		loadData = function(view, features) {
			var success;
			
			view = view || layer.core.view;
			features = features || layer.core.featureStore.features;

			var dimConf = gis.conf.finals.dimension,
				paramString = '?',
				dxItems = view.columns[0].items,
				isOperand = view.columns[0].dimension === dimConf.operand.objectName,
				peItems = view.filters[0].items,
				ouItems = view.rows[0].items;

			// ou
			paramString += 'dimension=ou:';

			for (var i = 0; i < ouItems.length; i++) {
				paramString += ouItems[i].id;
				paramString += i < ouItems.length - 1 ? ';' : '';
			}

			// dx
			paramString += '&dimension=dx:';

			for (var i = 0; i < dxItems.length; i++) {
				paramString += isOperand ? dxItems[i].id.split('-')[0] : dxItems[i].id;
				paramString += i < dxItems.length - 1 ? ';' : '';
			}

			paramString += isOperand ? '&dimension=co' : '';

			// pe
			paramString += '&filter=pe:';

			for (var i = 0; i < peItems.length; i++) {
				paramString += peItems[i].id;
				paramString += i < peItems.length - 1 ? ';' : '';
			}

			success = function(json) {
				var response = gis.api.response.Response(json),
					featureMap = {},
					valueMap = {},
					ouIndex,
					dxIndex,
					valueIndex,
					newFeatures = [],
					dimensions,
					items = [];

				if (!response) {
					olmap.mask.hide();
					return;
				}

				// ou index, value index
				for (var i = 0; i < response.headers.length; i++) {
					if (response.headers[i].name === dimConf.organisationUnit.dimensionName) {
						ouIndex = i;
					}
					else if (response.headers[i].name === dimConf.value.dimensionName) {
						valueIndex = i;
					}
				}

				// Feature map
				for (var i = 0, id; i < features.length; i++) {
					var id = features[i].attributes.id;

					featureMap[id] = true;
				}

				// Value map
				for (var i = 0; i < response.rows.length; i++) {
					var id = response.rows[i][ouIndex],
						value = parseFloat(response.rows[i][valueIndex]);

					valueMap[id] = value;
				}

				for (var i = 0; i < features.length; i++) {
					var feature = features[i],
						id = feature.attributes.id;

					if (featureMap.hasOwnProperty(id) && valueMap.hasOwnProperty(id)) {
						feature.attributes.value = valueMap[id];
						feature.attributes.label = feature.attributes.name + ' (' + feature.attributes.value + ')';
						newFeatures.push(feature);
					}
				}

				layer.removeFeatures(layer.features);
				layer.addFeatures(newFeatures);

				gis.response = response;

				loadLegend(view);
			};

			if (Ext.isObject(GIS.app)) {
				Ext.Ajax.request({
					url: gis.init.contextPath + '/api/analytics.json' + paramString,
					disableCaching: false,
					failure: function(r) {
						alert(r.responseText);
					},
					success: function(r) {
						success(Ext.decode(r.responseText));
					}
				});
			}
			else if (Ext.isObject(GIS.plugin)) {
				Ext.data.JsonP.request({
					url: gis.init.contextPath + '/api/analytics.jsonp' + paramString,
					disableCaching: false,
					scope: this,
					success: function(r) {
						success(r);
					}
				});
			}
		};

		loadLegend = function(view) {
			var bounds,
				addNames,
				fn;

			view = view || layer.core.view;

			addNames = function(response) {

				// All dimensions
				var dimensions = Ext.Array.clean([].concat(view.columns || [], view.rows || [], view.filters || [])),
					metaData = response.metaData,
					peIds = metaData[dimConf.period.objectName];

				for (var i = 0, dimension; i < dimensions.length; i++) {
					dimension = dimensions[i];

					for (var j = 0, item; j < dimension.items.length; j++) {
						item = dimension.items[j];

						if (item.id.indexOf('-') !== -1) {
							var ids = item.id.split('-');
							item.name = metaData.names[ids[0]] + ' ' + metaData.names[ids[1]];
						}
						else {
							item.name = metaData.names[item.id];
						}
					}
				}

				// Period name without changing the id
				view.filters[0].items[0].name = metaData.names[peIds[peIds.length - 1]];
			};

			fn = function() {
				addNames(gis.response);

				// Classification options
				var options = {
					indicator: gis.conf.finals.widget.value,
					method: view.legendSet ? mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS : view.method,
					numClasses: view.classes,
					bounds: bounds,
					colors: layer.core.getColors(view.colorLow, view.colorHigh),
					minSize: view.radiusLow,
					maxSize: view.radiusHigh
				};

				layer.core.view = view;
				layer.core.colorInterpolation = colors;
				layer.core.applyClassification(options);

				afterLoad(view);
			};

			// Labels
			if (view.labels) {
				if (Ext.isObject(view.labels)) {
					layer.styleMap = GIS.core.StyleMap(layer.id, view.labels);
				}
				else {
					layer.styleMap = GIS.core.StyleMap(layer.id, {
						fontSize: 12,
						strong: false,
						italic: false,
						color: '#000'
					});
				}
			}

			if (view.legendSet) {
				var bounds = [],
					colors = [],
					names = [],
					legends = [];

				Ext.Ajax.request({
					url: gis.init.contextPath + gis.conf.finals.url.path_api + 'mapLegendSets/' + view.legendSet.id + '.json?links=false&paging=false',
					scope: this,
					success: function(r) {
						legends = Ext.decode(r.responseText).mapLegends;

						Ext.Array.sort(legends, function (a, b) {
							return a.startValue - b.startValue;
						});

						for (var i = 0; i < legends.length; i++) {
							if (bounds[bounds.length - 1] !== legends[i].startValue) {
								if (bounds.length !== 0) {
									colors.push(new mapfish.ColorRgb(240,240,240));
									names.push('');
								}
								bounds.push(legends[i].startValue);
							}
							colors.push(new mapfish.ColorRgb());
							colors[colors.length - 1].setFromHex(legends[i].color);
							names.push(legends[i].name);
							bounds.push(legends[i].endValue);
						}

						view.legendSet.names = names;
						view.legendSet.bounds = bounds;
						view.legendSet.colors = colors;

						fn();
					}
				});
			}
			else {
				fn();
			}
		};

		afterLoad = function(view) {

			// Legend
			gis.viewport.eastRegion.doLayout();
			layer.legendPanel.expand();

			// Layer
			layer.setLayerOpacity(view.opacity);

			if (layer.item) {
				layer.item.setValue(true);
			}

			// Filter
			if (layer.filterWindow && layer.filterWindow.isVisible()) {
				layer.filterWindow.filter();
			}

			// Gui
			if (loader.updateGui && Ext.isObject(layer.widget)) {
				layer.widget.setGui(view);
			}

			// Zoom
			if (loader.zoomToVisibleExtent) {
				olmap.zoomToVisibleExtent();
			}

			// Mask
			if (loader.hideMask) {
				olmap.mask.hide();
			}

			// Map callback
			if (loader.callBack) {
				loader.callBack(layer);
			}
			else {
				gis.map = null;
				if (gis.viewport.shareButton) {
				}
			}

			// session storage
			if (GIS.isSessionStorage) {
				gis.util.layout.setSessionStorage('map', gis.util.layout.getAnalytical());
			}
		};

		loader = {
			compare: false,
			updateGui: false,
			zoomToVisibleExtent: false,
			hideMask: false,
			callBack: null,
			load: function(view) {
				gis.olmap.mask.show();

				if (this.compare) {
					compareView(view, true);
				}
				else {
					loadOrganisationUnits(view);
				}
			},
			loadData: loadData,
			loadLegend: loadLegend
		};

		return loader;
	};

	GIS.core.getInstance = function(init) {
		var conf = {},
			util = {},
			api = {},
			store = {},
			layers = [],
			gis = {};

		// conf
		(function() {
			conf.finals = {
				url: {
					path_api: '/api/',
					path_module: '/dhis-web-mapping/',
					path_commons: '/dhis-web-commons-ajax-json/',
					organisationunitchildren_get: 'getOrganisationUnitChildren.action',
					organisationunitgroup_getall: 'organisationUnitGroups.json?paging=false&links=false',
					dataset_get: 'dataSets.json?paging=false&links=false'
				},
				layer: {
					type_base: 'base',
					type_vector: 'vector',
					category_thematic: 'thematic'
				},
				dimension: {
					data: {
						id: 'data',
						value: 'data',
						param: 'dx',
						dimensionName: 'dx',
						objectName: 'dx'
					},
					category: {
						name: GIS.i18n.categories,
						dimensionName: 'co',
						objectName: 'co',
					},
					indicator: {
						id: 'indicator',
						value: 'indicators',
						param: 'in',
						dimensionName: 'dx',
						objectName: 'in'
					},
					dataElement: {
						id: 'dataElement',
						value: 'dataElement',
						param: 'de',
						dimensionName: 'dx',
						objectName: 'de'
					},
					operand: {
						id: 'operand',
						value: 'operand',
						param: 'dc',
						dimensionName: 'dx',
						objectName: 'dc'
					},
					dataSet: {
						value: 'dataSets',
						dimensionName: 'dx',
						objectName: 'ds'
					},
					period: {
						id: 'period',
						value: 'period',
						param: 'pe',
						dimensionName: 'pe',
						objectName: 'pe'
					},
					organisationUnit: {
						id: 'organisationUnit',
						value: 'organisationUnit',
						param: 'ou',
						dimensionName: 'ou',
						objectName: 'ou'
					},
					value: {
						id: 'value',
						value: 'value',
						param: 'value',
						dimensionName: 'value',
						objectName: 'value'
					}
				},
				widget: {
					value: 'value',
					legendtype_automatic: 'automatic',
					legendtype_predefined: 'predefined',
					symbolizer_color: 'color',
					symbolizer_image: 'image',
					loadtype_organisationunit: 'organisationUnit',
					loadtype_data: 'data',
					loadtype_legend: 'legend'
				},
				openLayers: {
					point_classname: 'OpenLayers.Geometry.Point'
				},
				mapfish: {
					classify_with_bounds: 1,
					classify_by_equal_intervals: 2,
					classify_by_quantils: 3
				},
				root: {
					id: 'root'
				}
			};

			conf.layout = {
				widget: {
					item_width: 288,
					itemlabel_width: 95,
					window_width: 310
				},
				tool: {
					item_width: 228,
					itemlabel_width: 95,
					window_width: 250
				},
				grid: {
					row_height: 27
				}
			};

			conf.period = {
				periodTypes: [
					{id: 'relativePeriods', name: GIS.i18n.relative},
					{id: 'Daily', name: GIS.i18n.daily},
					{id: 'Weekly', name: GIS.i18n.weekly},
					{id: 'Monthly', name: GIS.i18n.monthly},
					{id: 'BiMonthly', name: GIS.i18n.bimonthly},
					{id: 'Quarterly', name: GIS.i18n.quarterly},
					{id: 'SixMonthly', name: GIS.i18n.sixmonthly},
					{id: 'Yearly', name: GIS.i18n.yearly},
					{id: 'FinancialOct', name: GIS.i18n.financial_oct},
					{id: 'FinancialJuly', name: GIS.i18n.financial_july},
					{id: 'FinancialApril', name: GIS.i18n.financial_april}
				],
				relativePeriods: [
					{id: 'LAST_WEEK', name: GIS.i18n.last_week},
					{id: 'LAST_MONTH', name: GIS.i18n.last_month},
					{id: 'LAST_BIMONTH', name: GIS.i18n.last_bimonth},
					{id: 'LAST_QUARTER', name: GIS.i18n.last_quarter},
					{id: 'LAST_SIX_MONTH', name: GIS.i18n.last_sixmonth},
					{id: 'LAST_FINANCIAL_YEAR', name: GIS.i18n.last_financial_year},
					{id: 'THIS_YEAR', name: GIS.i18n.this_year},
					{id: 'LAST_YEAR', name: GIS.i18n.last_year}
				],
				relativePeriodsMap: {
					'LAST_WEEK': {id: 'LAST_WEEK', name: GIS.i18n.last_week},
					'LAST_MONTH': {id: 'LAST_MONTH', name: GIS.i18n.last_month},
					'LAST_BIMONTH': {id: 'LAST_BIMONTH', name: GIS.i18n.last_bimonth},
					'LAST_QUARTER': {id: 'LAST_QUARTER', name: GIS.i18n.last_quarter},
					'LAST_SIX_MONTH': {id: 'LAST_SIX_MONTH', name: GIS.i18n.last_sixmonth},
					'LAST_FINANCIAL_YEAR': {id: 'LAST_FINANCIAL_YEAR', name: GIS.i18n.last_financial_year},
					'THIS_YEAR': {id: 'THIS_YEAR', name: GIS.i18n.this_year},
					'LAST_YEAR': {id: 'LAST_YEAR', name: GIS.i18n.last_year}
				},
				integratedRelativePeriodsMap: {
					'LAST_WEEK': 'LAST_WEEK',
					'LAST_4_WEEKS': 'LAST_WEEK',
					'LAST_12_WEEKS': 'LAST_WEEK',
					'LAST_MONTH': 'LAST_MONTH',
					'LAST_3_MONTHS': 'LAST_MONTH',
					'LAST_12_MONTHS': 'LAST_MONTH',
					'LAST_BIMONTH': 'LAST_BIMONTH',
					'LAST_6_BIMONTHS': 'LAST_BIMONTH',
					'LAST_QUARTER': 'LAST_QUARTER',
					'LAST_4_QUARTERS': 'LAST_QUARTER',
					'LAST_SIX_MONTH': 'LAST_SIX_MONTH',
					'LAST_2_SIXMONTHS': 'LAST_SIX_MONTH',
					'LAST_FINANCIAL_YEAR': 'LAST_FINANCIAL_YEAR',
					'LAST_5_FINANCIAL_YEARS': 'LAST_FINANCIAL_YEAR',
					'THIS_YEAR': 'THIS_YEAR',
					'LAST_YEAR': 'LAST_YEAR',
					'LAST_5_YEARS': 'LAST_YEAR'
				}
			};
		}());

		// util
		(function() {
			util.map = {};

			util.map.getVisibleVectorLayers = function() {
				var layers = [];

				for (var i = 0, layer; i < gis.olmap.layers.length; i++) {
					layer = gis.olmap.layers[i];
					if (layer.layerType === conf.finals.layer.type_vector && layer.visibility && layer.features.length) {
						layers.push(layer);
					}
				}
				return layers;
			};

			util.map.getExtendedBounds = function(layers) {
				var bounds = null;
				if (layers.length) {
					bounds = layers[0].getDataExtent();
					if (layers.length > 1) {
						for (var i = 1; i < layers.length; i++) {
							bounds.extend(layers[i].getDataExtent());
						}
					}
				}
				return bounds;
			};

			util.map.zoomToVisibleExtent = function(olmap) {
				var bounds = util.map.getExtendedBounds(util.map.getVisibleVectorLayers(olmap));
				if (bounds) {
					olmap.zoomToExtent(bounds);
				}
			};

			util.map.getTransformedFeatureArray = function(features) {
				var sourceProjection = new OpenLayers.Projection("EPSG:4326"),
					destinationProjection = new OpenLayers.Projection("EPSG:900913");
				for (var i = 0; i < features.length; i++) {
					features[i].geometry.transform(sourceProjection, destinationProjection);
				}
				return features;
			};

			util.geojson = {};

			util.geojson.decode = function(doc) {
				var geojson = {};
				geojson.type = 'FeatureCollection';
				geojson.crs = {
					type: 'EPSG',
					properties: {
						code: '4326'
					}
				};
				geojson.features = [];

				for (var i = 0; i < doc.geojson.length; i++) {
					geojson.features.push({
						geometry: {
							type: parseInt(doc.geojson[i].ty) === 1 ? 'MultiPolygon' : 'Point',
							coordinates: doc.geojson[i].co
						},
						properties: {
							id: doc.geojson[i].uid,
							internalId: doc.geojson[i].iid,
							name: doc.geojson[i].na,
							hasCoordinatesDown: doc.geojson[i].hcd,
							hasCoordinatesUp: doc.geojson[i].hcu,
							level: doc.geojson[i].le,
							grandParentParentGraph: doc.geojson[i].gppg,
							grandParentId: doc.geojson[i].gpuid,
							parentGraph: doc.geojson[i].parentGraph,
							parentId: doc.geojson[i].pi,
							parentName: doc.geojson[i].pn
						}
					});
				}

				return geojson;
			};

			util.gui = {};
			util.gui.combo = {};

			util.gui.combo.setQueryMode = function(cmpArray, mode) {
				for (var i = 0; i < cmpArray.length; i++) {
					cmpArray[i].queryMode = mode;
				}
			};

			util.object = {};

			util.object.sortObjectsByString = function(array, key) {
				key = key || 'name';
				array.sort( function(a, b) {
					var nameA = a[key].toLowerCase(),
						nameB = b[key].toLowerCase();

					if (nameA < nameB) {
						return -1;
					}
					if (nameA > nameB) {
						return 1;
					}
					return 0;
				});
				return array;
			};

			util.object.getLength = function(object) {
				var size = 0;

				for (var key in object) {
					if (object.hasOwnProperty(key)) {
						size++;
					}
				}

				return size;
			};
		}());

		gis.init = init;
		gis.conf = conf;
		gis.util = util;

		// api
		(function() {
			var dimConf = gis.conf.finals.dimension;

			api.layout = {};
			api.response = {};

			api.layout.Record = function(config) {
				var record = {};

				// id: string

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Record config is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.id)) {
						alert('Record id is not text: ' + config);
						return;
					}

					record.id = config.id.replace('.', '-');

					if (Ext.isString(config.name)) {
						record.name = config.name;
					}

					return Ext.clone(record);
				}();
			};

			api.layout.Dimension = function(config) {
				var dimension = {};

				// dimension: string

				// items: [Record]

				return function() {
					if (!Ext.isObject(config)) {
						//console.log('Dimension config is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.dimension)) {
						console.log('Dimension name is not text: ' + config);
						return;
					}

					if (config.dimension !== conf.finals.dimension.category.objectName) {
						var records = [];

						if (!Ext.isArray(config.items)) {
							console.log('Dimension items is not an array: ' + config);
							return;
						}

						for (var i = 0; i < config.items.length; i++) {
							record = api.layout.Record(config.items[i]);

							if (record) {
								records.push(record);
							}
						}

						config.items = records;

						if (!config.items.length) {
							console.log('Dimension has no valid items: ' + config);
							return;
						}
					}

					dimension.dimension = config.dimension;
					dimension.items = config.items;

					return Ext.clone(dimension);
				}();
			};

			api.layout.Layout = function(config) {
				var config = Ext.clone(config),
					layout = {},
					getValidatedDimensionArray,
					validateSpecialCases;

				// layer: string

				// columns: [Dimension]

				// rows: [Dimension]

				// filters: [Dimension]

				// classes: integer (5) - 1-7

				// method: integer (2) - 2, 3 // 2=equal intervals, 3=equal counts

				// colorLow: string ('ff0000')

				// colorHigh: string ('00ff00')

				// radiusLow: integer (5)

				// radiusHigh: integer (15)

				// opacity: integer (0.8) - 0-1

				// legendSet: object

				getValidatedDimensionArray = function(dimensionArray) {
					var dimensions = [];

					if (!(dimensionArray && Ext.isArray(dimensionArray) && dimensionArray.length)) {
						return;
					}

					for (var i = 0, dimension; i < dimensionArray.length; i++) {
						dimension = api.layout.Dimension(dimensionArray[i]);

						if (dimension) {
							dimensions.push(dimension);
						}
					}

					dimensionArray = dimensions;

					return dimensionArray.length ? dimensionArray : null;
				};

				validateSpecialCases = function(config) {
					var dimensions = Ext.Array.clean([].concat(config.columns || [], config.rows || [], config.filters || [])),
						map = conf.period.integratedRelativePeriodsMap,
						dxDim,
						peDim,
						ouDim;

					for (var i = 0, dim; i < dimensions.length; i++) {
						dim = dimensions[i];

						if (dim.dimension === dimConf.indicator.objectName ||
							dim.dimension === dimConf.dataElement.objectName ||
							dim.dimension === dimConf.operand.objectName ||
							dim.dimension === dimConf.dataSet.objectName) {
							dxDim = dim;
						}
						else if (dim.dimension === dimConf.period.objectName) {
							peDim = dim;
						}
						else if (dim.dimension === dimConf.organisationUnit.objectName) {
							ouDim = dim;
						}
					}

					if (!ouDim) {
						alert('No organisation units specified');
						return;
					}

					if (dxDim) {
						dxDim.items = [dxDim.items[0]];
					}

					if (peDim) {
						peDim.items = [peDim.items[0]];
						peDim.items[0].id = map[peDim.items[0].id] ? map[peDim.items[0].id] : peDim.items[0].id;
					}

					config.columns = [dxDim];
					config.rows = [ouDim];
					config.filters = [peDim];

					return config;
				};

				return function() {
					var a = [],
						objectNames = [],
						dimConf = conf.finals.dimension,
						isOu = false,
						isOuc = false,
						isOugc = false;

					config = validateSpecialCases(config);

					if (!config) {
						return;
					}

					config.columns = getValidatedDimensionArray(config.columns);
					config.rows = getValidatedDimensionArray(config.rows);
					config.filters = getValidatedDimensionArray(config.filters);

					if (!config.rows) {
						console.log('Organisation unit dimension is invalid');
						return;
					}

					// Collect object names and user orgunits
					for (var i = 0, dim, dims = Ext.Array.clean([].concat(config.columns, config.rows, config.filters)); i < dims.length; i++) {
						dim = dims[i];

						if (dim) {

							// Object names
							if (Ext.isString(dim.dimension)) {
								objectNames.push(dim.dimension);
							}

							// user orgunits
							if (dim.dimension === dimConf.organisationUnit.objectName && Ext.isArray(dim.items)) {
								for (var j = 0; j < dim.items.length; j++) {
									if (dim.items[j].id === 'USER_ORGUNIT') {
										isOu = true;
									}
									else if (dim.items[j].id === 'USER_ORGUNIT_CHILDREN') {
										isOuc = true;
									}
									else if (dim.items[j].id === 'USER_ORGUNIT_GRANDCHILDREN') {
										isOugc = true;
									}
								}
							}
						}
					}

					// Layout
					layout.columns = config.columns;
					layout.rows = config.rows;
					layout.filters = config.filters;

					// Properties
					layout.layer = Ext.isString(config.layer) && !Ext.isEmpty(config.layer) ? config.layer : 'thematic1';
					layout.classes = Ext.isNumber(config.classes) && !Ext.isEmpty(config.classes) ? config.classes : 5;
					layout.method = Ext.isNumber(config.method) && !Ext.isEmpty(config.method) ? config.method : 2;
					layout.colorLow = Ext.isString(config.colorLow) && !Ext.isEmpty(config.colorLow) ? config.colorLow : 'ff0000';
					layout.colorHigh = Ext.isString(config.colorHigh) && !Ext.isEmpty(config.colorHigh) ? config.colorHigh : '00ff00';
					layout.radiusLow = Ext.isNumber(config.radiusLow) && !Ext.isEmpty(config.radiusLow) ? config.radiusLow : 5;
					layout.radiusHigh = Ext.isNumber(config.radiusHigh) && !Ext.isEmpty(config.radiusHigh) ? config.radiusHigh : 15;
					layout.opacity = Ext.isNumber(config.opacity) && !Ext.isEmpty(config.opacity) ? config.opacity : 0.8;

					layout.userOrganisationUnit = isOu;
					layout.userOrganisationUnitChildren = isOuc;
					layout.userOrganisationUnitGrandChildren = isOugc;

					layout.parentGraphMap = Ext.isObject(config.parentGraphMap) ? config.parentGraphMap : null;

					layout.legendSet = config.legendSet;
					layout.labels = config.labels;

					layout.organisationUnitGroupSet = config.organisationUnitGroupSet;
					layout.areaRadius = config.areaRadius;

					return layout;
				}();
			};

			api.response.Header = function(config) {
				var header = {};

				// name: string

				// meta: boolean

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Header is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.name)) {
						console.log('Header name is not text: ' + config);
						return;
					}

					if (!Ext.isBoolean(config.meta)) {
						console.log('Header meta is not boolean: ' + config);
						return;
					}

					header.name = config.name;
					header.meta = config.meta;

					return Ext.clone(header);
				}();
			};

			api.response.Response = function(config) {
				var response = {};

				// headers: [Header]

				return function() {
					var headers = [];

					if (!(config && Ext.isObject(config))) {
						alert('Data response invalid');
						return false;
					}

					if (!(config.headers && Ext.isArray(config.headers))) {
						alert('Data response invalid');
						return false;
					}

					for (var i = 0, header; i < config.headers.length; i++) {
						header = api.response.Header(config.headers[i]);

						if (header) {
							headers.push(header);
						}
					}

					config.headers = headers;

					if (!config.headers.length) {
						alert('No valid response headers');
						return;
					}

					if (!(Ext.isArray(config.rows) && config.rows.length > 0)) {
						alert('No values found');
						return false;
					}

					if (config.headers.length !== config.rows[0].length) {
						alert('Data invalid');
						return false;
					}

					response.headers = config.headers;
					response.metaData = config.metaData;
					response.width = config.width;
					response.height = config.height;
					response.rows = config.rows;

					return response;
				}();
			};
		}());

		// store
		(function() {
			store.organisationUnitLevels = GIS.core.OrganisationUnitLevelStore(gis);
		}());

		gis.api = api;
		gis.store = store;

		gis.olmap = GIS.core.getOLMap(gis);
		gis.layer = GIS.core.getLayers(gis);
		gis.thematicLayers = [gis.layer.thematic1, gis.layer.thematic2, gis.layer.thematic3, gis.layer.thematic4];

		if (window.google) {
			layers.push(gis.layer.googleStreets, gis.layer.googleHybrid);
		}

		layers.push(
			gis.layer.openStreetMap,
			gis.layer.thematic4,
			gis.layer.thematic3,
			gis.layer.thematic2,
			gis.layer.thematic1,
			gis.layer.boundary,
			gis.layer.facility,
			gis.layer.event
		);

		gis.olmap.addLayers(layers);

		GIS.core.instances.push(gis);
e = gis.layer.event;

		return gis;
	};


	// MAPFISH (mapfish.js)

	(function() {
	window.mapfish = {

        /**
         * Property: _scriptName
         * {String} Relative path of this script.
         */
        _scriptName: "MapFish.js",

        /**
         * Function: _getScriptLocation
         * Return the path to this script.
         *
         * Returns:
         * Path to this script
         */
        _getScriptLocation: function () {
            // Workaround for Firefox bug:
            // https://bugzilla.mozilla.org/show_bug.cgi?id=351282
            if (window.gMfLocation) {
                return window.gMfLocation;
            }

            var scriptLocation = "";
            var scriptName = mapfish._scriptName;

            var scripts = document.getElementsByTagName('script');
            for (var i = 0; i < scripts.length; i++) {
                var src = scripts[i].getAttribute('src');
                if (src) {
                    var index = src.lastIndexOf(scriptName);
                    // is it found, at the end of the URL?
                    if ((index > -1) && (index + scriptName.length == src.length)) {
                        scriptLocation = src.slice(0, -scriptName.length);
                        break;
                    }
                }
            }
            return scriptLocation;
         }
    };

	var jsfiles = new Array(
		"core/Color.js",
		"core/GeoStat.js",
		"core/GeoStat/Boundary.js",
		"core/GeoStat/Thematic1.js",
		"core/GeoStat/Thematic2.js",
		"core/GeoStat/Facility.js",
		"core/GeoStat/Symbol.js",
		"core/Util.js"
		//"widgets/geostat/Boundary.js",
		//"widgets/geostat/Thematic1.js",
		//"widgets/geostat/Thematic2.js",
		//"widgets/geostat/Facility.js",
		//"widgets/geostat/Symbol.js"
	);

	var allScriptTags = "";
	var host = mapfish._getScriptLocation();

	for (var i = 0; i < jsfiles.length; i++) {
		if (/MSIE/.test(navigator.userAgent) || /Safari/.test(navigator.userAgent)) {
			var currentScriptTag = "<script src='" + host + jsfiles[i] + "'></script>";
			allScriptTags += currentScriptTag;
		} else {
			var s = document.createElement("script");
			s.src = host + jsfiles[i];
			var h = document.getElementsByTagName("head").length ?
					   document.getElementsByTagName("head")[0] :
					   document.body;
			h.appendChild(s);
		}
	}
	if (allScriptTags) {
		//document.write(allScriptTags);
	}


	// MAPFISH COLOR (color.js)

	/**
	 * An abstract representation of color.
	 */
	mapfish.Color = OpenLayers.Class({
		getColorRgb: function() {}
	});

	/**
	 * Class: mapfish.ColorRgb
	 * Class for representing RGB colors.
	 */
	mapfish.ColorRgb = OpenLayers.Class(mapfish.Color, {
		redLevel: null,
		greenLevel: null,
		blueLevel: null,

		/**
		 * Constructor: mapfish.ColorRgb
		 *
		 * Parameters:
		 * red - {Integer}
		 * green - {Integer}
		 * blue - {Integer}
		 */
		initialize: function(red, green, blue) {
			this.redLevel = red;
			this.greenLevel = green;
			this.blueLevel = blue;
		},

		/**
		 * APIMethod: equals
		 *      Returns true if the colors at the same.
		 *
		 * Parameters:
		 * {<mapfish.ColorRgb>} color
		 */
		equals: function(color) {
			return color.redLevel == this.redLevel &&
				   color.greenLevel == this.greenLevel &&
				   color.blueLevel == this.blueLevel;
		},

		getColorRgb: function() {
			return this;
		},

		getRgbArray: function() {
			return [
				this.redLevel,
				this.greenLevel,
				this.blueLevel
			];
		},

		/**
		 * Method: hex2rgbArray
		 * Converts a Hex color string to an Rbg array
		 *
		 * Parameters:
		 * rgbHexString - {String} Hex color string (format: #rrggbb)
		 */
		hex2rgbArray: function(rgbHexString) {
			if (rgbHexString.charAt(0) == '#') {
				rgbHexString = rgbHexString.substr(1);
			}
			var rgbArray = [
				parseInt(rgbHexString.substring(0,2),16),
				parseInt(rgbHexString.substring(2,4),16),
				parseInt(rgbHexString.substring(4,6),16)
			];
			for (var i = 0; i < rgbArray.length; i++) {
				if (rgbArray[i] < 0 || rgbArray[i] > 255 ) {
					OpenLayers.Console.error("Invalid rgb hex color string: rgbHexString");
				}
			}
			return rgbArray;
		},

		/**
		 * APIMethod: setFromHex
		 * Sets the color from a color hex string
		 *
		 * Parameters:
		 * rgbHexString - {String} Hex color string (format: #rrggbb)
		 */
		setFromHex: function(rgbHexString) {
			var rgbArray = this.hex2rgbArray(rgbHexString);
			this.redLevel = rgbArray[0];
			this.greenLevel = rgbArray[1];
			this.blueLevel = rgbArray[2];
		},

		/**
		 * APIMethod: setFromRgb
		 * Sets the color from a color rgb string
		 *
		 */
		setFromRgb: function(rgbString) {
			var color = dojo.colorFromString(rgbString);
			this.redLevel = color.r;
			this.greenLevel = color.g;
			this.blueLevel = color.b;
		},

		/**
		 * APIMethod: toHexString
		 * Converts the rgb color to hex string
		 *
		 */
		toHexString: function() {
			var r = this.toHex(this.redLevel);
			var g = this.toHex(this.greenLevel);
			var b = this.toHex(this.blueLevel);
			return '#' + r + g + b;
		},

		/**
		 * Method: toHex
		 * Converts a color level to its hexadecimal value
		 *
		 * Parameters:
		 * dec - {Integer} Decimal value to convert [0..255]
		 */
		toHex: function(dec) {
			// create list of hex characters
			var hexCharacters = "0123456789ABCDEF";
			// if number is out of range return limit
			if (dec < 0 || dec > 255 ) {
				var msg = "Invalid decimal value for color level";
				OpenLayers.Console.error(msg);
			}
			// decimal equivalent of first hex character in converted number
			var i = Math.floor(dec / 16);
			// decimal equivalent of second hex character in converted number
			var j = dec % 16;
			// return hexadecimal equivalent
			return hexCharacters.charAt(i) + hexCharacters.charAt(j);
		},

		CLASS_NAME: "mapfish.ColorRgb"
	});

	/**
	 * APIMethod: getColorsArrayByRgbInterpolation
	 *      Get an array of colors based on RGB interpolation.
	 *
	 * Parameters:
	 * firstColor - {<mapfish.Color>} The first color in the range.
	 * lastColor - {<mapfish.Color>} The last color in the range.
	 * nbColors - {Integer} The number of colors in the range.
	 *
	 * Returns
	 * {Array({<mapfish.Color>})} The resulting array of colors.
	 */
	mapfish.ColorRgb.getColorsArrayByRgbInterpolation = function(firstColor, lastColor, nbColors) {
		var resultColors = [];
		var colorA = firstColor.getColorRgb();
		var colorB = lastColor.getColorRgb();
		var colorAVal = colorA.getRgbArray();
		var colorBVal = colorB.getRgbArray();
		if (nbColors == 1) {
			return [colorA];
		}
		for (var i = 0; i < nbColors; i++) {
			var rgbTriplet = [];
			rgbTriplet[0] = colorAVal[0] +
				i * (colorBVal[0] - colorAVal[0]) / (nbColors - 1);
			rgbTriplet[1] = colorAVal[1] +
				i * (colorBVal[1] - colorAVal[1]) / (nbColors - 1);
			rgbTriplet[2] = colorAVal[2] +
				i * (colorBVal[2] - colorAVal[2]) / (nbColors - 1);
			resultColors[i] = new mapfish.ColorRgb(parseInt(rgbTriplet[0]),
				parseInt(rgbTriplet[1]), parseInt(rgbTriplet[2]));
		}
		return resultColors;
	};


	// MAPFISH UTIL (util.js)

	/**
	 * Namespace: mapfish.Util
	 * Utility functions
	 */
	mapfish.Util = {};

	/**
	 * APIFunction: sum
	 * Return the sum of the elements of an array.
	 */
	mapfish.Util.sum = function(array) {
		for (var i=0, sum=0; i < array.length; sum += array[i++]);
		return sum;
	};

	/**
	 * APIFunction: max
	 * Return the max of the elements of an array.
	 */
	mapfish.Util.max = function(array) {
		return Math.max.apply({}, array);
	};

	/**
	 * APIFunction: min
	 * Return the min of the elements of an array.
	 */
	mapfish.Util.min = function(array) {
		return Math.min.apply({}, array);
	};

	/**
	 * Function: getIconUrl
	 * Builds the URL for a layer icon, based on a WMS GetLegendGraphic request.
	 *
	 * Parameters:
	 * wmsUrl - {String} The URL of a WMS server.
	 * options - {Object} The options to set in the request:
	 *                    'layer' - the name of the layer for which the icon is requested (required)
	 *                    'rule' - the name of a class for this layer (this is set to the layer name if not specified)
	 *                    'format' - "image/png" by default
	 *                    ...
	 *
	 * Returns:
	 * {String} The URL at which the icon can be found.
	 */
	mapfish.Util.getIconUrl = function(wmsUrl, options) {
		if (!options.layer) {
			OpenLayers.Console.warn(
				'Missing required layer option in mapfish.Util.getIconUrl');
			return '';
		}
		if (!options.rule) {
			options.rule = options.layer;
		}
		if (wmsUrl.indexOf("?") < 0) {
			//add a ? to the end of the url if it doesn't
			//already contain one
			wmsUrl += "?";
		} else if (wmsUrl.lastIndexOf('&') != (wmsUrl.length - 1)) {
			//if there was already a ? , assure that the parameters
			//are ended with an &, except if the ? was at the last char
			if (wmsUrl.indexOf("?") != (wmsUrl.length - 1)) {
				wmsUrl += "&";
			}
		}
		var options = OpenLayers.Util.extend({
			layer: "",
			rule: "",
			service: "WMS",
			version: "1.1.1",
			request: "GetLegendGraphic",
			format: "image/png",
			width: 16,
			height: 16
		}, options);
		options = OpenLayers.Util.upperCaseObject(options);
		return wmsUrl + OpenLayers.Util.getParameterString(options);
	};


	/**
	 * APIFunction: arrayEqual
	 * Compare two arrays containing primitive types.
	 *
	 * Parameters:
	 * a - {Array} 1st to be compared.
	 * b - {Array} 2nd to be compared.
	 *
	 * Returns:
	 * {Boolean} True if both given arrays contents are the same (elements value and type).
	 */
	mapfish.Util.arrayEqual = function(a, b) {
		if(a == null || b == null)
			return false;
		if(typeof(a) != 'object' || typeof(b) != 'object')
			return false;
		if (a.length != b.length)
			return false;
		for (var i = 0; i < a.length; i++) {
			if (typeof(a[i]) != typeof(b[i]))
				return false;
			if (a[i] != b[i])
				return false;
		}
		return true;
	};

	/**
	 * Function: isIE7
	 *
	 * Returns:
	 * {Boolean} True if the browser is Internet Explorer V7
	 */
	mapfish.Util.isIE7 = function () {
		var ua = navigator.userAgent.toLowerCase();
		return ua.indexOf("msie 7") > -1;
	};

	/**
	 * APIFunction: relativeToAbsoluteURL
	 *
	 * Parameters:
	 * source - {String} the source URL
	 *
	 * Returns:
	 * {String} An absolute URL
	 */
	mapfish.Util.relativeToAbsoluteURL = function(source) {
		if (/^\w+:/.test(source) || !source) {
			return source;
		}

		var h = location.protocol + "//" + location.host;
		if (source.indexOf("/") == 0) {
			return h + source;
		}

		var p = location.pathname.replace(/\/[^\/]*$/, '');
		return h + p + "/" + source;
	};

	/**
	 * Function: fixArray
	 *
	 * In some fields, OpenLayers allows to use a coma separated string instead
	 * of an array. This method make sure we end up with an array.
	 *
	 * Parameters:
	 * subs - {String/Array}
	 *
	 * Returns:
	 * {Array}
	 */
	mapfish.Util.fixArray = function(subs) {
		if (subs == '' || subs == null) {
			return [];
		} else if (subs instanceof Array) {
			return subs;
		} else {
			return subs.split(',');
		}
	};


	// MAPFISH GEOSTAT (geostat.js)

	/**
	 * @requires OpenLayers/Layer/Vector.js
	 * @requires OpenLayers/Popup/AnchoredBubble.js
	 * @requires OpenLayers/Feature/Vector.js
	 * @requires OpenLayers/Format/GeoJSON.js
	 * @requires OpenLayers/Control/SelectFeature.js
	 * @requires OpenLayers/Ajax.js
	 */

	mapfish.GeoStat = OpenLayers.Class({

		layer: null,

		format: null,

		url: null,

		requestSuccess: function(request) {},

		requestFailure: function(request) {},

		indicator: null,

		defaultSymbolizer: {},

		legendDiv: null,

		initialize: function(map, options) {
			this.map = map;
			this.addOptions(options);
			if (!this.layer) {
				var layer = new OpenLayers.Layer.Vector('geostat', {
					'displayInLayerSwitcher': false,
					'visibility': false
				});
				map.addLayer(layer);
				this.layer = layer;
			}

			this.setUrl(this.url);
			this.legendDiv = Ext.get(options.legendDiv);
		},

		setUrl: function(url) {
			this.url = url;
			if (this.url) {
				OpenLayers.Request.GET({
					url: this.url,
					scope: this,
					success: this.requestSuccess,
					failure: this.requestFailure
				});
			}
		},

		getColors: function(low, high) {
			var startColor = new mapfish.ColorRgb(),
				endColor = new mapfish.ColorRgb()
			startColor.setFromHex(low);
			endColor.setFromHex(high);
			return [startColor, endColor];
		},

		addOptions: function(newOptions) {
			if (newOptions) {
				if (!this.options) {
					this.options = {};
				}
				// update our copy for clone
				OpenLayers.Util.extend(this.options, newOptions);
				// add new options to this
				OpenLayers.Util.extend(this, newOptions);
			}
		},

		extendStyle: function(rules, symbolizer, context) {
			var style = this.layer.styleMap.styles['default'];
			if (rules) {
				style.rules = rules;
			}
			if (symbolizer) {
				style.setDefaultStyle(
					OpenLayers.Util.applyDefaults(
						symbolizer,
						style.defaultStyle
					)
				);
			}
			if (context) {
				if (!style.context) {
					style.context = {};
				}
				OpenLayers.Util.extend(style.context, context);
			}
		},

		applyClassification: function(options) {
			this.layer.renderer.clear();
			this.layer.redraw();
			this.updateLegend();
			this.layer.setVisibility(true);
		},

		showDetails: function(obj) {},

		hideDetails: function(obj) {},

		CLASS_NAME: "mapfish.GeoStat"
	});

	mapfish.GeoStat.Distribution = OpenLayers.Class({

		labelGenerator: function(bin, binIndex, nbBins) {
			var lower = parseFloat(bin.lowerBound).toFixed(1),
				upper = parseFloat(bin.upperBound).toFixed(1);
			return lower + ' - ' + upper + '&nbsp;&nbsp;' + '(' + bin.nbVal + ')';
		},

		values: null,

		nbVal: null,

		minVal: null,

		maxVal: null,

		initialize: function(values, options) {
			OpenLayers.Util.extend(this, options);
			this.values = values;
			this.nbVal = values.length;
			this.minVal = this.nbVal ? mapfish.Util.min(this.values) : 0;
			this.maxVal = this.nbVal ? mapfish.Util.max(this.values) : 0;
		},

		classifyWithBounds: function(bounds) {
			var bins = [];
			var binCount = [];
			var sortedValues = [];

			for (var i = 0; i < this.values.length; i++) {
				sortedValues.push(this.values[i]);
			}
			sortedValues.sort(function(a,b) {return a-b;});
			var nbBins = bounds.length - 1;

			for (var j = 0; j < nbBins; j++) {
				binCount[j] = 0;
			}

			for (var k = 0; k < nbBins - 1; k) {
				if (sortedValues[0] < bounds[k + 1]) {
					binCount[k] = binCount[k] + 1;
					sortedValues.shift();
				} else {
					k++;
				}
			}

			binCount[nbBins - 1] = this.nbVal - mapfish.Util.sum(binCount);

			for (var m = 0; m < nbBins; m++) {
				bins[m] = new mapfish.GeoStat.Bin(binCount[m], bounds[m], bounds[m + 1], m == (nbBins - 1));
				var labelGenerator = this.labelGenerator || this.defaultLabelGenerator;
				bins[m].label = labelGenerator(bins[m], m, nbBins);
			}

			return new mapfish.GeoStat.Classification(bins);
		},

		classifyByEqIntervals: function(nbBins) {
			var bounds = [];

			for (var i = 0; i <= nbBins; i++) {
				bounds[i] = this.minVal + i*(this.maxVal - this.minVal) / nbBins;
			}

			return this.classifyWithBounds(bounds);
		},

		classifyByQuantils: function(nbBins) {
			var values = this.values;
			values.sort(function(a,b) {return a-b;});
			var binSize = Math.round(this.values.length / nbBins);

			var bounds = [];
			var binLastValPos = (binSize === 0) ? 0 : binSize;

			if (values.length > 0) {
				bounds[0] = values[0];
				for (i = 1; i < nbBins; i++) {
					bounds[i] = values[binLastValPos];
					binLastValPos += binSize;
				}
				bounds.push(values[values.length - 1]);
			}

			for (var j = 0; j < bounds.length; j++) {
				bounds[j] = parseFloat(bounds[j]);
			}

			return this.classifyWithBounds(bounds);
		},

		sturgesRule: function() {
			return Math.floor(1 + 3.3 * Math.log(this.nbVal, 10));
		},

		classify: function(method, nbBins, bounds) {
			var classification = null;
			if (!nbBins) {
				nbBins = this.sturgesRule();
			}

			switch (parseFloat(method)) {
			case mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS :
				classification = this.classifyWithBounds(bounds);
				break;
			case mapfish.GeoStat.Distribution.CLASSIFY_BY_EQUAL_INTERVALS :
				classification = this.classifyByEqIntervals(nbBins);
				break;
			case mapfish.GeoStat.Distribution.CLASSIFY_BY_QUANTILS :
				classification = this.classifyByQuantils(nbBins);
				break;
			default:
				OpenLayers.Console.error("Unsupported or invalid classification method");
			}
			return classification;
		},

		CLASS_NAME: "mapfish.GeoStat.Distribution"
	});

	mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS = 1;

	mapfish.GeoStat.Distribution.CLASSIFY_BY_EQUAL_INTERVALS = 2;

	mapfish.GeoStat.Distribution.CLASSIFY_BY_QUANTILS = 3;

	mapfish.GeoStat.Bin = OpenLayers.Class({
		label: null,
		nbVal: null,
		lowerBound: null,
		upperBound: null,
		isLast: false,

		initialize: function(nbVal, lowerBound, upperBound, isLast) {
			this.nbVal = nbVal;
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
			this.isLast = isLast;
		},

		CLASS_NAME: "mapfish.GeoStat.Bin"
	});

	mapfish.GeoStat.Classification = OpenLayers.Class({
		bins: [],

		initialize: function(bins) {
			this.bins = bins;
		},

		getBoundsArray: function() {
			var bounds = [];
			for (var i = 0; i < this.bins.length; i++) {
				bounds.push(this.bins[i].lowerBound);
			}
			if (this.bins.length > 0) {
				bounds.push(this.bins[this.bins.length - 1].upperBound);
			}
			return bounds;
		},

		CLASS_NAME: "mapfish.GeoStat.Classification"
	});


	// MAPFISH ALL (all.js)

	/**
	 * @requires core/GeoStat.js
	 */

	mapfish.GeoStat.Event = OpenLayers.Class(mapfish.GeoStat, {

		colors: [new mapfish.ColorRgb(120, 120, 0), new mapfish.ColorRgb(255, 0, 0)],
		method: mapfish.GeoStat.Distribution.CLASSIFY_BY_QUANTILS,
		numClasses: 5,
		minSize: 4,
		maxSize: 4,
		minVal: null,
		maxVal: null,
		defaultSymbolizer: {'fillOpacity': 1},
		classification: null,
		colorInterpolation: null,

		gis: null,
		view: null,
		featureStore: Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			features: [],
			loadFeatures: function(features) {
				if (features && features.length) {
					var data = [];
					for (var i = 0; i < features.length; i++) {
						data.push([features[i].attributes.id, features[i].attributes.name]);
					}
					this.loadData(data);
					this.sortStore();

					this.features = features;
				}
				else {
					this.removeAll();
				}
			},
			sortStore: function() {
				this.sort('name', 'ASC');
			}
		}),

		initialize: function(map, options) {
			mapfish.GeoStat.prototype.initialize.apply(this, arguments);
		},

		getLoader: function() {
			return GIS.core.LayerLoaderEvent(this.gis, this.layer);
		},

		reset: function() {
			this.layer.destroyFeatures();

			if (this.layer.widget) {
				this.layer.widget.reset();
			}
		},

		extendView: function(view, config) {
			view = view || this.view;

			view.organisationUnitLevel = config.organisationUnitLevel || view.organisationUnitLevel;
			view.parentOrganisationUnit = config.parentOrganisationUnit || view.parentOrganisationUnit;
			view.parentLevel = config.parentLevel || view.parentLevel;
			view.parentGraph = config.parentGraph || view.parentGraph;
			view.opacity = config.opacity || view.opacity;

			return view;
		},

		getLegendConfig: function() {
			return;
		},

		getImageLegendConfig: function() {
			return;
		},

		updateOptions: function(newOptions) {
			var oldOptions = OpenLayers.Util.extend({}, this.options);
			this.addOptions(newOptions);
			if (newOptions) {
				this.setClassification();
			}
		},

		createColorInterpolation: function() {
			var numColors = this.classification.bins.length;

			this.colorInterpolation = mapfish.ColorRgb.getColorsArrayByRgbInterpolation(this.colors[0], this.colors[1], numColors);
		},

		setClassification: function() {
			var values = [];
			for (var i = 0; i < this.layer.features.length; i++) {
				values.push(this.layer.features[i].attributes[this.indicator]);
			}

			var distOptions = {
				labelGenerator: this.options.labelGenerator
			};
			var dist = new mapfish.GeoStat.Distribution(values, distOptions);

			this.minVal = dist.minVal;
			this.maxVal = dist.maxVal;

			this.classification = dist.classify(
				this.method,
				this.numClasses,
				null
			);

			this.createColorInterpolation();
		},

		applyClassification: function(options) {
			this.updateOptions(options);

			var calculateRadius = OpenLayers.Function.bind(
				function(feature) {
					var value = feature.attributes[this.indicator];
					var size = (value - this.minVal) / (this.maxVal - this.minVal) *
						(this.maxSize - this.minSize) + this.minSize;
					return size || this.minSize;
				},	this
			);
			this.extendStyle(null, {'pointRadius': '${calculateRadius}'}, {'calculateRadius': calculateRadius});

			var boundsArray = this.classification.getBoundsArray();
			var rules = new Array(boundsArray.length-1);
			for (var i = 0; i < boundsArray.length-1; i++) {
				var rule = new OpenLayers.Rule({
					symbolizer: {fillColor: this.colorInterpolation[i].toHexString()},
					filter: new OpenLayers.Filter.Comparison({
						type: OpenLayers.Filter.Comparison.BETWEEN,
						property: this.indicator,
						lowerBoundary: boundsArray[i],
						upperBoundary: boundsArray[i + 1]
					})
				});
				rules[i] = rule;
			}

			this.extendStyle(rules);
			mapfish.GeoStat.prototype.applyClassification.apply(this, arguments);
		},

		updateLegend: function() {

		},

		CLASS_NAME: "mapfish.GeoStat.Event"
	});

	mapfish.GeoStat.Boundary = OpenLayers.Class(mapfish.GeoStat, {

		colors: [new mapfish.ColorRgb(120, 120, 0), new mapfish.ColorRgb(255, 0, 0)],
		method: mapfish.GeoStat.Distribution.CLASSIFY_BY_QUANTILS,
		numClasses: 5,
		minSize: 3,
		maxSize: 20,
		minVal: null,
		maxVal: null,
		defaultSymbolizer: {'fillOpacity': 1},
		classification: null,
		colorInterpolation: null,

		gis: null,
		view: null,
		featureStore: Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			features: [],
			loadFeatures: function(features) {
				if (features && features.length) {
					var data = [];
					for (var i = 0; i < features.length; i++) {
						data.push([features[i].attributes.id, features[i].attributes.name]);
					}
					this.loadData(data);
					this.sortStore();

					this.features = features;
				}
				else {
					this.removeAll();
				}
			},
			sortStore: function() {
				this.sort('name', 'ASC');
			}
		}),

		initialize: function(map, options) {
			mapfish.GeoStat.prototype.initialize.apply(this, arguments);
		},

		getLoader: function() {
			return GIS.core.LayerLoaderBoundary(this.gis, this.layer);
		},

		reset: function() {
			this.layer.destroyFeatures();

			if (this.layer.widget) {
				this.layer.widget.reset();
			}
		},

		extendView: function(view, config) {
			view = view || this.view;

			view.organisationUnitLevel = config.organisationUnitLevel || view.organisationUnitLevel;
			view.parentOrganisationUnit = config.parentOrganisationUnit || view.parentOrganisationUnit;
			view.parentLevel = config.parentLevel || view.parentLevel;
			view.parentGraph = config.parentGraph || view.parentGraph;
			view.opacity = config.opacity || view.opacity;

			return view;
		},

		getLegendConfig: function() {
			return;
		},

		getImageLegendConfig: function() {
			return;
		},

		updateOptions: function(newOptions) {
			var oldOptions = OpenLayers.Util.extend({}, this.options);
			this.addOptions(newOptions);
			if (newOptions) {
				this.setClassification();
			}
		},

		createColorInterpolation: function() {
			var numColors = this.classification.bins.length;

			this.colorInterpolation = mapfish.ColorRgb.getColorsArrayByRgbInterpolation(this.colors[0], this.colors[1], numColors);
		},

		setClassification: function() {
			var values = [];
			for (var i = 0; i < this.layer.features.length; i++) {
				values.push(this.layer.features[i].attributes[this.indicator]);
			}

			var distOptions = {
				labelGenerator: this.options.labelGenerator
			};
			var dist = new mapfish.GeoStat.Distribution(values, distOptions);

			this.minVal = dist.minVal;
			this.maxVal = dist.maxVal;

			this.classification = dist.classify(
				this.method,
				this.numClasses,
				null
			);

			this.createColorInterpolation();
		},

		applyClassification: function(options) {
			this.updateOptions(options);

			var calculateRadius = OpenLayers.Function.bind(
				function(feature) {
					var value = feature.attributes[this.indicator];
					var size = (value - this.minVal) / (this.maxVal - this.minVal) *
						(this.maxSize - this.minSize) + this.minSize;
					return size || this.minSize;
				},	this
			);
			this.extendStyle(null, {'pointRadius': '${calculateRadius}'}, {'calculateRadius': calculateRadius});

			var boundsArray = this.classification.getBoundsArray();
			var rules = new Array(boundsArray.length-1);
			for (var i = 0; i < boundsArray.length-1; i++) {
				var rule = new OpenLayers.Rule({
					symbolizer: {fillColor: this.colorInterpolation[i].toHexString()},
					filter: new OpenLayers.Filter.Comparison({
						type: OpenLayers.Filter.Comparison.BETWEEN,
						property: this.indicator,
						lowerBoundary: boundsArray[i],
						upperBoundary: boundsArray[i + 1]
					})
				});
				rules[i] = rule;
			}

			this.extendStyle(rules);
			mapfish.GeoStat.prototype.applyClassification.apply(this, arguments);
		},

		updateLegend: function() {

		},

		CLASS_NAME: "mapfish.GeoStat.Boundary"
	});

	mapfish.GeoStat.Facility = OpenLayers.Class(mapfish.GeoStat, {

		classification: null,

		gis: null,
		view: null,
		featureStore: Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			features: [],
			loadFeatures: function(features) {
				if (features && features.length) {
					var data = [];
					for (var i = 0; i < features.length; i++) {
						data.push([features[i].attributes.id, features[i].attributes.name]);
					}
					this.loadData(data);
					this.sortStore();

					this.features = features;
				}
				else {
					this.removeAll();
				}
			},
			sortStore: function() {
				this.sort('name', 'ASC');
			}
		}),

		initialize: function(map, options) {
			mapfish.GeoStat.prototype.initialize.apply(this, arguments);
		},

		getLoader: function() {
			return GIS.core.LayerLoaderFacility(this.gis, this.layer);
		},

		decode: function(doc) {
			var feature,
				group,
				attr,
				geojson = {
					type: 'FeatureCollection',
					crs: {
						type: 'EPSG',
						properties: {
							code: '4326'
						}
					},
					features: []
				};

			for (var i = 0; i < doc.geojson.length; i++) {
				attr = doc.geojson[i];

				feature = {
					geometry: {
						type: parseInt(attr.ty) === 1 ? 'MultiPolygon' : 'Point',
						coordinates: attr.co
					},
					properties: {
						id: attr.uid,
						internalId: attr.iid,
						name: attr.na
					}
				};
				feature.properties = Ext.Object.merge(feature.properties, attr.groupSets);

				geojson.features.push(feature);
			}

			return geojson;
		},

		reset: function() {
			this.layer.destroyFeatures();

			// Legend
			this.layer.legendPanel.update('');
			this.layer.legendPanel.collapse();

			if (this.layer.widget) {
				this.layer.widget.reset();
			}
		},

		extendView: function(view, config) {
			view = view || this.view;

			view.organisationUnitGroupSet = config.organisationUnitGroupSet || view.organisationUnitGroupSet;
			view.organisationUnitLevel = config.organisationUnitLevel || view.organisationUnitLevel;
			view.parentOrganisationUnit = config.parentOrganisationUnit || view.parentOrganisationUnit;
			view.parentLevel = config.parentLevel || view.parentLevel;
			view.parentGraph = config.parentGraph || view.parentGraph;
			view.opacity = config.opacity || view.opacity;

			return view;
		},

		updateOptions: function(newOptions) {
			this.addOptions(newOptions);
		},

		applyClassification: function(options) {
			this.updateOptions(options);

			var items = this.gis.store.groupsByGroupSet.data.items;

			var rules = new Array(items.length);
			for (var i = 0; i < items.length; i++) {
				var rule = new OpenLayers.Rule({
					symbolizer: {
						'pointRadius': 8,
						'externalGraphic': '../../images/orgunitgroup/' + items[i].data.symbol
					},
					filter: new OpenLayers.Filter.Comparison({
						type: OpenLayers.Filter.Comparison.EQUAL_TO,
						property: this.indicator,
						value: items[i].data.name
					})
				});
				rules[i] = rule;
			}

			this.extendStyle(rules);
			mapfish.GeoStat.prototype.applyClassification.apply(this, arguments);
		},

		updateLegend: function() {
			var	element = document.createElement("div"),
				child = document.createElement("div"),
				items = this.gis.store.groupsByGroupSet.data.items;

			for (var i = 0; i < items.length; i++) {
				child = document.createElement("div");
				child.style.backgroundImage = 'url(../../images/orgunitgroup/' + items[i].data.symbol + ')';
				child.style.backgroundRepeat = 'no-repeat';
				child.style.width = "21px";
				child.style.height = "16px";
				child.style.marginBottom = "2px";
				child.style.cssFloat = "left";
				element.appendChild(child);

				child = document.createElement("div");
				child.innerHTML = items[i].data.name;
				child.style.height = "16px";
				child.style.lineHeight = "17px";
				element.appendChild(child);

				child = document.createElement("div");
				child.style.clear = "left";
				element.appendChild(child);
			}

			this.layer.legendPanel.update(element.outerHTML);
		},

		CLASS_NAME: "mapfish.GeoStat.Facility"
	});

	mapfish.GeoStat.createThematic = function(name) {

		mapfish.GeoStat[name] = OpenLayers.Class(mapfish.GeoStat, {
			colors: [new mapfish.ColorRgb(120, 120, 0), new mapfish.ColorRgb(255, 0, 0)],
			method: mapfish.GeoStat.Distribution.CLASSIFY_BY_QUANTILS,
			numClasses: 5,
			bounds: null,
			minSize: 3,
			maxSize: 20,
			minVal: null,
			maxVal: null,
			defaultSymbolizer: {'fillOpacity': 1},
			classification: null,
			colorInterpolation: null,

			gis: null,
			view: null,
			featureStore: Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				features: [],
				loadFeatures: function(features) {
					if (features && features.length) {
						var data = [];
						for (var i = 0; i < features.length; i++) {
							data.push([features[i].attributes.id, features[i].attributes.name]);
						}
						this.loadData(data);
						this.sortStore();

						this.features = features;
					}
					else {
						this.removeAll();
					}
				},
				sortStore: function() {
					this.sort('name', 'ASC');
				}
			}),

			initialize: function(map, options) {
				mapfish.GeoStat.prototype.initialize.apply(this, arguments);
			},

			getLoader: function() {
				return GIS.core.LayerLoaderThematic(this.gis, this.layer);
			},

			reset: function() {
				this.layer.destroyFeatures();
				this.featureStore.loadFeatures(this.layer.features.slice(0));

				// Legend
				this.layer.legendPanel.update('');
				this.layer.legendPanel.collapse();

				// Widget
				if (this.layer.widget) {
					this.layer.widget.reset();
				}
			},

			extendView: function(view, config) {
				view = view || this.view;

				view.valueType = config.valueType || view.valueType;
				view.indicatorGroup = config.indicatorGroup || view.indicatorGroup;
				view.indicator = config.indicator || view.indicator;
				view.dataElementGroup = config.dataElementGroup || view.dataElementGroup;
				view.dataElement = config.dataElement || view.dataElement;
				view.periodType = config.periodType || view.periodType;
				view.period = config.period || view.period;
				view.legendType = config.legendType || view.legendType;
				view.legendSet = config.legendSet || view.legendSet;
				view.classes = config.classes || view.classes;
				view.method = config.method || view.method;
				view.colorLow = config.colorLow || view.colorLow;
				view.colorHigh = config.colorHigh || view.colorHigh;
				view.radiusLow = config.radiusLow || view.radiusLow;
				view.radiusHigh = config.radiusHigh || view.radiusHigh;
				view.organisationUnitLevel = config.organisationUnitLevel || view.organisationUnitLevel;
				view.parentOrganisationUnit = config.parentOrganisationUnit || view.parentOrganisationUnit;
				view.parentLevel = config.parentLevel || view.parentLevel;
				view.parentGraph = config.parentGraph || view.parentGraph;
				view.opacity = config.opacity || view.opacity;

				return view;
			},

			getImageLegendConfig: function() {
				var bins = this.classification.bins,
					rgb = this.colorInterpolation,
					config = [];

				for (var i = 0; i < bins.length; i++) {
					config.push({
						color: rgb[i].toHexString(),
						label: bins[i].lowerBound.toFixed(1) + ' - ' + bins[i].upperBound.toFixed(1) + ' (' + bins[i].nbVal + ')'
					});
				}

				return config;
			},

			updateOptions: function(newOptions) {
				var oldOptions = OpenLayers.Util.extend({}, this.options);
				this.addOptions(newOptions);
				if (newOptions) {
					this.setClassification();
				}
			},

			createColorInterpolation: function() {
				var numColors = this.classification.bins.length;

				if (!this.view.legendSet) {
					this.colorInterpolation = mapfish.ColorRgb.getColorsArrayByRgbInterpolation(this.colors[0], this.colors[1], numColors);
				}
			},

			setClassification: function() {
				var values = [];
				for (var i = 0; i < this.layer.features.length; i++) {
					values.push(this.layer.features[i].attributes[this.indicator]);
				}

				var distOptions = {
					labelGenerator: this.options.labelGenerator
				};
				var dist = new mapfish.GeoStat.Distribution(values, distOptions);

				this.minVal = dist.minVal;
				this.maxVal = dist.maxVal;

				if (this.view.legendType === this.gis.conf.finals.widget.legendtype_predefined) {
					if (this.bounds[0] > this.minVal) {
						this.bounds.unshift(this.minVal);
						//if (this.widget == centroid) { this.widget.symbolizerInterpolation.unshift('blank');
						this.colorInterpolation.unshift(new mapfish.ColorRgb(240,240,240));
					}

					if (this.bounds[this.bounds.length-1] < this.maxVal) {
						this.bounds.push(this.maxVal);
						//todo if (this.widget == centroid) { G.vars.activeWidget.symbolizerInterpolation.push('blank');
						this.colorInterpolation.push(new mapfish.ColorRgb(240,240,240));
					}
				}

				this.classification = dist.classify(
					this.method,
					this.numClasses,
					this.bounds
				);

				this.createColorInterpolation();
			},

			applyClassification: function(options, legend) {
				this.updateOptions(options, legend);

				var calculateRadius = OpenLayers.Function.bind(
					function(feature) {
						var value = feature.attributes[this.indicator];
						var size = (value - this.minVal) / (this.maxVal - this.minVal) *
							(this.maxSize - this.minSize) + this.minSize;
						return size || this.minSize;
					},	this
				);
				this.extendStyle(null, {'pointRadius': '${calculateRadius}'}, {'calculateRadius': calculateRadius});

				var boundsArray = this.classification.getBoundsArray();
				var rules = new Array(boundsArray.length - 1);
				for (var i = 0; i < boundsArray.length - 1; i++) {
					var rule = new OpenLayers.Rule({
						symbolizer: {
							fillColor: this.colorInterpolation[i].toHexString()
						},
						filter: new OpenLayers.Filter.Comparison({
							type: OpenLayers.Filter.Comparison.BETWEEN,
							property: this.indicator,
							lowerBoundary: boundsArray[i],
							upperBoundary: boundsArray[i + 1]
						})
					});
					rules[i] = rule;
				}

				this.extendStyle(rules);
				mapfish.GeoStat.prototype.applyClassification.apply(this, arguments);
			},

			updateLegend: function() {
				var	element = document.createElement("div"),
					child,
					legendNames;

				// data
				child = document.createElement("div");
				child.style.height = "14px";
				child.style.overflow = "hidden";
				child.title = this.view.columns[0].items[0].name;
				child.innerHTML = this.view.columns[0].items[0].name;
				element.appendChild(child);

				child = document.createElement("div");
				child.style.clear = "left";
				element.appendChild(child);

				// period
				child = document.createElement("div");
				child.style.height = "14px";
				child.style.overflow = "hidden";
				child.title = this.view.filters[0].items[0].name;
				child.innerHTML = this.view.filters[0].items[0].name;
				element.appendChild(child);

				child = document.createElement("div");
				child.style.clear = "left";
				element.appendChild(child);

				// separator
				child = document.createElement("div");
				child.style.width = "1px";
				child.style.height = "5px";
				element.appendChild(child);

				// legends
				if (this.view.legendSet) {
					for (var i = 0; i < this.classification.bins.length; i++) {
						child = document.createElement("div");
						child.style.backgroundColor = this.colorInterpolation[i].toHexString();
						child.style.width = "30px";
						child.style.height = this.view.legendSet.names[i] ? "25px" : "20px";
						child.style.cssFloat = "left";
						child.style.marginRight = "8px";
						element.appendChild(child);

						child = document.createElement("div");
						child.style.lineHeight = this.view.legendSet.names[i] ? "12px" : "7px";
						child.innerHTML = '<b style="color:#222; font-size:10px !important">' + (this.view.legendSet.names[i] || '') + '</b><br/>' + this.classification.bins[i].label;
						element.appendChild(child);

						child = document.createElement("div");
						child.style.clear = "left";
						element.appendChild(child);
					}
				}
				else {
					for (var i = 0; i < this.classification.bins.length; i++) {
						child = document.createElement("div");
						child.style.backgroundColor = this.colorInterpolation[i].toHexString();
						child.style.width = "30px";
						child.style.height = "15px";
						child.style.cssFloat = "left";
						child.style.marginRight = "8px";
						element.appendChild(child);

						child = document.createElement("div");
						child.innerHTML = this.classification.bins[i].label;
						element.appendChild(child);

						child = document.createElement("div");
						child.style.clear = "left";
						element.appendChild(child);
					}
				}

				this.layer.legendPanel.update(element.outerHTML);
			},

			CLASS_NAME: "mapfish.GeoStat." + name
		});
	};

	mapfish.GeoStat.createThematic('Thematic1');
	mapfish.GeoStat.createThematic('Thematic2');
	mapfish.GeoStat.createThematic('Thematic3');
	mapfish.GeoStat.createThematic('Thematic4');

	}());


	// GIS PLUGIN (plugin.js)
	var init = {
			user: {}
		},
		configs = [],
		isInitStarted = false,
		isInitComplete = false,
		getInit,
		applyCss,
		execute;

	GIS.i18n = {
		facility_layer_legend: 'Facility layer legend',
		thematic_layer_1_legend: 'Thematic layer 1 legend',
		thematic_layer_2_legend: 'Thematic layer 2 legend',
		thematic_layer_3_legend: 'Thematic layer 3 legend',
		thematic_layer_4_legend: 'Thematic layer 4 legend',
		measure_distance: 'Measure distance'
	};

	GIS.plugin = {};

	getInit = function(url) {
		var isInit = false,
			requests = [],
			callbacks = 0,
			fn;

		fn = function() {
			if (++callbacks === requests.length) {
				isInitComplete = true;

				for (var i = 0; i < configs.length; i++) {
					execute(configs[i]);
				}

				configs = [];
			}
		};

		requests.push({
			url: url + '/api/system/context.jsonp',
			success: function(r) {
				init.contextPath = r.contextPath;
				fn();
			}
		});

		requests.push({
			url: url + '/api/organisationUnits.jsonp?userOnly=true&viewClass=detailed&links=false',
			success: function(r) {
				var organisationUnits = r.organisationUnits || [];
				
				if (organisationUnits.length) {
					var ou = organisationUnits[0];

					if (ou.id) {
						init.user = {
							ou: ou.id
						};

						init.user.ouc = ou.children ? Ext.Array.pluck(ou.children, 'id') : null;
					};
				}
				else {
					console.log('User is not assigned to any organisation units');
				}
				fn();
			}
		});

		requests.push({
			url: url + '/api/mapLegendSets.jsonp?viewClass=detailed&links=false&paging=false',
			success: function(r) {
				init.legendSets = Ext.isArray(r.mapLegendSets) ? r.mapLegendSets : [];
				fn();
			}
		});

		requests.push({
			url: url + '/api/dimensions.jsonp?links=false&paging=false',
			success: function(r) {
				init.dimensions = Ext.isArray(r.dimensions) ? r.dimensions : [];
				fn();
			}
		});

		for (var i = 0; i < requests.length; i++) {
			Ext.data.JsonP.request(requests[i]);
		}
	};

	applyCss = function() {
		var css = '.gis-plugin, .gis-plugin * { font-family: arial, sans-serif, liberation sans, consolas; } \n';
		css += '.x-panel-body { font-size: 11px; } \n';
		css += '.x-panel-header { height: 30px; padding: 7px 4px 4px 7px; border: 0 none; } \n';
		css += '.gis-container-default .x-window-body { padding: 5px; background: #fff; } \n';
		css += '.olControlPanel { position: absolute; top: 0; right: 0; border: 0 none; } \n';
		css += '.olControlButtonItemActive { background: #556; color: #fff; width: 24px; height: 24px; opacity: 0.75; filter: alpha(opacity=75); -ms-filter: "alpha(opacity=75)"; cursor: pointer; cursor: hand; text-align: center; font-size: 21px !important; text-shadow: 0 0 1px #ddd; } \n';
		css += '.olControlPanel.zoomIn { right: 72px; } \n';
		css += '.olControlPanel.zoomIn .olControlButtonItemActive { border-bottom-left-radius: 2px; } \n';
		css += '.olControlPanel.zoomOut { right: 48px; } \n';
		css += '.olControlPanel.zoomVisible { right: 24px; } \n';
		css += '.olControlPermalink { display: none !important; } \n';
		css += '.olControlMousePosition { background: #fff !important; opacity: 0.8 !important; filter: alpha(opacity=80) !important; -ms-filter: "alpha(opacity=80)" !important; right: 0 !important; bottom: 0 !important; border-top-left-radius: 2px !important; padding: 2px 2px 2px 5px !important; color: #000 !important; -webkit-text-stroke-width: 0.2px; -webkit-text-stroke-color: #555; } \n';
		css += '.olControlMousePosition * { font-size: 10px !important; } \n';
		css += '.text-mouseposition-lonlat { color: #555; } \n';
		css += '.olLayerGoogleCopyright, .olLayerGoogleV3.olLayerGooglePoweredBy { display: none; } \n';
		css += '#google-logo { background: url("' + init.contextPath + '/dhis-web-mapping/app/images/google-logo.png") no-repeat; width: 40px; height: 13px; margin-left: 6px; display: inline-block; vertical-align: bottom; cursor: pointer; cursor: hand; } \n';
		css += '.olControlScaleLine { left: 5px !important; bottom: 5px !important; } \n';
		css += '.olControlScaleLineBottom { display: none; } \n';
		css += '.olControlScaleLineTop { font-weight: bold; } \n';
		css += '.x-mask-msg { padding: 0; border: 0 none; background-image: none; background-color: transparent; } \n';
		css += '.x-mask-msg div { background-position: 11px center; } \n';
		css += '.x-mask-msg .x-mask-loading { border: 0 none; background-color: #000; color: #fff; border-radius: 2px; padding: 12px 14px 12px 30px; opacity: 0.65; } \n';
		css += '.gis-window-widget-feature { padding: 0; border: 0 none; border-radius: 0; background: transparent; box-shadow: none; } \n';
		css += '.gis-window-widget-feature .x-window-body-default { border: 0 none; background: transparent; } \n';
		css += '.gis-window-widget-feature .x-window-body-default .x-panel-body-default { border: 0 none; background: #556; opacity: 0.92; filter: alpha(opacity=92); -ms-filter: "alpha(opacity=92)"; padding: 5px 8px 5px 8px; border-bottom-left-radius: 2px; border-bottom-right-radius: 2px; color: #fff; font-weight: bold; letter-spacing: 1px; } \n';
		css += '.x-menu-body { border:1px solid #bbb; border-radius: 2px; padding: 0; background-color: #fff !important; } \n';
		css += '.x-menu-item-active .x-menu-item-link {	border-radius: 0; border-color: #e1e1e1; background-color: #e1e1e1; background-image: none; } \n';
		css += '.x-menu-item-link { padding: 4px 5px 4px 26px; } \n';
		css += '.x-menu-item-text { color: #111; } \n';
		css += '.disabled { opacity: 0.4; cursor: default !important; } \n';
		css += '.el-opacity-1 { opacity: 1 !important; } \n';
		css += '.el-border-0, .el-border-0 .x-panel-body { border: 0 none !important; } \n';
		css += '.el-fontsize-10 { font-size: 10px !important; } \n';
		css += '.gis-grid-row-icon-disabled * { cursor: default !important; } \n';
		css += '.gis-toolbar-btn-menu { margin-top: 4px; } \n';
		css += '.gis-toolbar-btn-menu .x-panel-body-default { border-radius: 2px; border-color: #999; } \n';
		css += '.gis-grid .link, .gis-grid .link * { cursor: pointer; cursor: hand; color: blue; text-decoration: underline; } \n';
		css += '.gis-menu-item-icon-drill, .gis-menu-item-icon-float { left: 6px; } \n';
		css += '.gis-menu-item-first.x-menu-item-active .x-menu-item-link {	border-radius: 0; border-top-left-radius: 2px; border-top-right-radius: 2px; } \n';
		css += '.gis-menu-item-last.x-menu-item-active .x-menu-item-link { border-radius: 0; border-bottom-left-radius: 2px; border-bottom-right-radius: 2px; } \n';
		css += '.gis-menu-item-icon-drill { \n background: url("' + init.contextPath + '/dhis-web-mapping/app/images/drill_16.png") no-repeat; } \n';
		css += '.gis-menu-item-icon-float { background: url("' + init.contextPath + '/dhis-web-mapping/app/images/float_16.png") no-repeat; } \n';
		css += '.x-color-picker a { padding: 0; } \n';
		css += '.x-color-picker em span { width: 14px; height: 14px; } \n';

		Ext.util.CSS.createStyleSheet(css);
	};

	execute = function(config) {
		var validateConfig,
			createViewport,
			afterRender,
			initialize,
			gis;

		validateConfig = function() {
			if (!Ext.isString(config.url)) {
				alert('Invalid url (' + config.el + ')');
				return;
			}

			if (config.url.split('').pop() === '/') {
				config.url = config.url.substr(0, config.url.length - 1);
			}

			if (!Ext.isString(config.el)) {
				alert('Invalid html element id (' + config.el + ')');
				return;
			}

			config.id = config.id || config.uid;

			if (config.id && !Ext.isString(config.id)) {
				alert('Invalid map id (' + config.el + ')');
				return;
			}

			return true;
		};

		createViewport = function() {
			var viewport,
				eastRegion,
				centerRegion,
				el = Ext.get(gis.el);

			viewport = Ext.create('Ext.panel.Panel', {
				renderTo: el,
				width: el.getWidth(),
				height: el.getHeight(),
				cls: 'gis-plugin',
				layout: {
					type: 'hbox',
					align: 'stretch'
				},
				items: [
					{
						xtype: 'gx_mappanel',
						map: gis.olmap,
						bodyStyle: 'border:0 none',
						width: el.getWidth() - (gis.map.hideLegend ? 0 : 200),
						height: el.getHeight(),
						listeners: {
							added: function() {
								centerRegion = this;
							}
						}
					},
					{
						xtype: 'panel',
						layout: 'anchor',
						bodyStyle: 'border-top:0 none; border-bottom:0 none',
						width: gis.map.hideLegend ? 0 : 200,
						preventHeader: true,
						defaults: {
							bodyStyle: 'padding: 6px; border: 0 none',
							collapsible: true,
							collapsed: true,
							animCollapse: false
						},
						items: [
							{
								title: GIS.i18n.thematic_layer_1_legend,
								bodyStyle: 'padding:3px 0 4px 5px; border-width:1px 0 1px 0; border-color:#d0d0d0;',
								listeners: {
									added: function() {
										gis.layer.thematic1.legendPanel = this;
									}
								}
							},
							{
								title: GIS.i18n.thematic_layer_2_legend,
								bodyStyle: 'padding:3px 0 4px 5px; border-width:1px 0 1px 0; border-color:#d0d0d0;',
								listeners: {
									added: function() {
										gis.layer.thematic2.legendPanel = this;
									}
								}
							},
							{
								title: GIS.i18n.thematic_layer_3_legend,
								bodyStyle: 'padding:3px 0 4px 5px; border-width:1px 0 1px 0; border-color:#d0d0d0;',
								listeners: {
									added: function() {
										gis.layer.thematic3.legendPanel = this;
									}
								}
							},
							{
								title: GIS.i18n.thematic_layer_4_legend,
								bodyStyle: 'padding:3px 0 4px 5px; border-width:1px 0 1px 0; border-color:#d0d0d0;',
								listeners: {
									added: function() {
										gis.layer.thematic4.legendPanel = this;
									}
								}
							},
							{
								title: GIS.i18n.facility_layer_legend,
								bodyStyle: 'padding:3px 0 4px 5px; border-width:1px 0 1px 0; border-color:#d0d0d0;',
								listeners: {
									added: function() {
										gis.layer.facility.legendPanel = this;
									}
								}
							}
						],
						listeners: {
							added: function() {
								eastRegion = this;
							}
						}
					}
				],
				listeners: {
					afterrender: function() {
						afterRender();
					}
				}
			});

			viewport.centerRegion = centerRegion;
			viewport.eastRegion = eastRegion;

			return viewport;
		};

		afterRender = function(vp) {
			var len = Ext.query('.zoomInButton').length;

			for (var i = 0; i < len; i++) {
				Ext.query('.zoomInButton')[i].innerHTML = '<img src="' + gis.init.contextPath + '/dhis-web-mapping/app/images/zoomin_24.png" />';
				Ext.query('.zoomOutButton')[i].innerHTML = '<img src="' + gis.init.contextPath + '/dhis-web-mapping/app/images/zoomout_24.png" />';
				Ext.query('.zoomVisibleButton')[i].innerHTML = '<img src="' + gis.init.contextPath + '/dhis-web-mapping/app/images/zoomvisible_24.png" />';
				Ext.query('.measureButton')[i].innerHTML = '<img src="' + gis.init.contextPath + '/dhis-web-mapping/app/images/measure_24.png" />';
			}

			// base layer
			if (Ext.isDefined(gis.map.baseLayer)) {
				var base = Ext.isString(gis.map.baseLayer) ? gis.map.baseLayer.split(' ').join('').toLowerCase() : gis.map.baseLayer;

				if (!base || base === 'none' || base === 'off') {
					gis.layer.googleStreets.setVisibility(false);
				}
				else if (base === 'gh' || base === 'googlehybrid') {
					gis.olmap.setBaseLayer(gis.layer.googleHybrid);
				}
				else if (base === 'osm' || base === 'openstreetmap') {
					gis.olmap.setBaseLayer(gis.layer.openStreetMap);
				}
			}
		};

		initialize = function() {
			if (!validateConfig()) {
				return;
			}

			applyCss();

			gis = GIS.core.getInstance(init);
			gis.el = config.el;

			GIS.core.createSelectHandlers(gis, gis.layer.boundary);
			GIS.core.createSelectHandlers(gis, gis.layer.thematic1);
			GIS.core.createSelectHandlers(gis, gis.layer.thematic2);
			GIS.core.createSelectHandlers(gis, gis.layer.thematic3);
			GIS.core.createSelectHandlers(gis, gis.layer.thematic4);
			GIS.core.createSelectHandlers(gis, gis.layer.facility);

			gis.map = config;

			gis.viewport = createViewport();

			gis.olmap.mask = Ext.create('Ext.LoadMask', gis.viewport.centerRegion.getEl(), {
				msg: 'Loading'
			});

			GIS.core.MapLoader(gis).load();
		}();
	};

	GIS.plugin.getMap = function(config) {
		if (Ext.isString(config.url) && config.url.split('').pop() === '/') {
			config.url = config.url.substr(0, config.url.length - 1);
		}

		if (isInitComplete) {
			execute(config);
		}
		else {
			configs.push(config);

			if (!isInitStarted) {
				isInitStarted = true;
				getInit(config.url);
			}
		}
	};

	DHIS = Ext.isObject(window['DHIS']) ? DHIS : {};
	DHIS.getMap = GIS.plugin.getMap;

});
