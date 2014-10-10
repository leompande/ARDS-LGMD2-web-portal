TR.conf = {
    init: {
		ajax: {
			jsonfy: function(r) {
				r = Ext.JSON.decode(r.responseText);
				var obj = { 
					system: {
						maxLevels: r.levels.length
					}
				};
				obj.system.rootnodes = [];
				for (var i = 0; i < r.user.ous.length; i++) {
					obj.system.rootnodes.push({id: r.user.ous[i].id, name: r.user.ous[i].name});
				}
				
				obj.system.user = {id: r.user.id, name: r.user.name};
				
				obj.system.program = [];
				for (var i = 0; i < r.programs.length; i++) {
					obj.system.program.push({id: r.programs[i].id, name: r.programs[i].name, type: r.programs[i].type, localid: r.programs[i].localid});
				}
				
				obj.system.orgunitGroup = [];
				for (var i = 0; i < r.orgunitGroups.length; i++) {
					obj.system.orgunitGroup.push({id: r.orgunitGroups[i].id, name: r.orgunitGroups[i].name });
				}
				
				obj.report={};
				obj.report.id = r.id;
				
				return obj;
			}
		}
    },
    finals: {
        ajax: {
			path_lib: '../../dhis-web-commons/javascripts/',
            path_root: '../',
            path_commons: '../',
            path_api: '../../api/',
            path_images: 'images/',
			initialize: 'tabularInitialize.action',
			programstages_get: 'programs/',
			programstagesections_get: 'loadProgramStageSections.action',
			dataelements_get: 'loadDataElements.action',
			organisationunitchildren_get: 'getOrganisationUnitChildren.action',
			organisationunit_getbygroup: 'getOrganisationUnitPathsByGroup.action',
			generatetabularreport_get: 'analytics/events/query/',
			casebasedfavorite_getall: 'getTabularReports.action',
			casebasedfavorite_get: 'getTabularReport.action',
			casebasedfavorite_rename: 'updateTabularReportName.action',
			casebasedfavorite_validate: 'validateTabularReport.action',
			casebasedfavorite_save: 'saveTabularReport.action',
            casebasedfavorite_delete: 'deleteTabularReport.action',
			suggested_dataelement_get: 'getOptions.action',
			aggregatefavorite_getall: 'getAggregateReportList.action',
			aggregatefavorite_get: 'getAggregateReport.action',
			aggregatefavorite_rename: 'updateAggregateReportName.action',
			aggregatefavorite_save: 'saveAggregateReport.action',
            aggregatefavorite_delete: 'deleteAggregateReport.action',
			aggregatefavorite_validate: 'validateAggregateReport.action',
			generateaggregatereport_get: 'analytics/events/aggregate/',
			username_dataelement_get: 'getUsernameList.action',
			organisationunit_getbyids: 'getOrganisationUnitPaths.action',
			redirect: 'index.action'
        },
        params: {
            data: {
                value: 'data',
                rawvalue: TR.i18n.regular_program,
                warning: {
					filter: TR.i18n.wm_multiple_filter_ind_de
				}
            },
            program: {
                value: 'program',
                rawvalue: TR.i18n.program
            },
            organisationunit: {
                value: 'organisationunit',
                rawvalue: TR.i18n.organisation_unit,
                warning: {
					filter: TR.i18n.wm_multiple_filter_orgunit
				}
            },
            programStage: {
                value: 'programStage',
                rawvalue: TR.i18n.program_stage
            },
            dataelement: {
                value: 'dataelement',
                rawvalue: TR.i18n.data_elements
            }
        },
        data: {
			domain: 'domain_',
		},
		root: {
			id: 'root'
		},
		download: {
            xls: 'xls',
			csv: 'csv'
        },
        cmd: {
            init: 'init_',
            none: 'none_',
			urlparam: 'id'
        }
    },
	reportPosition: {
		POSITION_ROW_ORGUNIT_COLUMN_PERIOD: 1,
		POSITION_ROW_PERIOD_COLUMN_ORGUNIT: 2,
		POSITION_ROW_ORGUNIT_ROW_PERIOD: 3,
		POSITION_ROW_PERIOD: 4,
		POSITION_ROW_ORGUNIT: 5,
		POSITION_ROW_PERIOD_COLUMN_DATA: 6,
		POSITION_ROW_ORGUNIT_COLUMN_DATA: 7,
		POSITION_ROW_DATA: 8,
		POSITION_ROW_DATA_COLUMN_PERIOD: 9,
		POSITION_ROW_DATA_COLUMN_ORGUNIT: 10
	},
    userOrgunit: {
		USER_ORGUNIT: 'USER_ORGUNIT',
		USER_ORGUNIT_CHILDREN: 'USER_ORGUNIT_CHILDREN',
		USER_ORGUNIT_GRANDCHILDREN : 'USER_ORGUNIT_GRANDCHILDREN'
	},
	statusbar: {
		icon: {
			error: 'error_s.png',
			warning: 'warning.png',
			ok: 'ok.png'
		}
	},
    layout: {
        west_width: 424,
        west_fieldset_width: 402,
		west_multiselect: 100,
        west_width_subtractor: 18,
        west_fill: 117,
        west_fill_accordion_organisationunit: 43,
        west_maxheight_accordion_organisationunit: 225,
        center_tbar_height: 30,
        east_gridcolumn_height: 30,
        form_label_width: 90,
		grid_row_height: 27,
		grid_favorite_width: 450,
		grid_favorite_height: 500,
        window_favorite_ypos: 100,
        window_confirm_width: 250,
		window_record_width: 450,
		window_record_height: 300,
		west_dataelements_multiselect: 120,
		west_dataelements_filter_panel: 250,
		west_dataelements_expand_filter_panel: 280,
		west_dataelements_collapse_filter_panel: 130,
		west_dataelements_expand_aggregate_filter_panel: 230,
		west_dataelements_collapse_aggregate_filter_panel: 80,
		west_properties_multiselect: 150,
		west_properties_filter_panel: 130,
		west_properties_expand_filter_panel: 280,
		west_properties_collapse_filter_panel: 135
    },
	util: {
		jsonEncodeString: function(str) {
			return typeof str === 'string' ? str.replace(/[^a-zA-Z 0-9(){}<>_!+;:?*&%#-]+/g,'') : str;
		},
		jsonEncodeArray: function(a) {
			for (var i = 0; i < a.length; i++) {
				a[i] = TR.conf.util.jsonEncodeString(a[i]);
			}
			return a;
		},
		getURLParameters: function(paramName) {
			var sURL = window.document.URL.toString();  
			if (sURL.indexOf("?") > 0)
			{
			   var arrParams = sURL.split("?");         
			   var arrURLParams = arrParams[1].split("&");      
			   var arrParamNames = new Array(arrURLParams.length);
			   var arrParamValues = new Array(arrURLParams.length);     
			   var i = 0;
			   for (i=0;i<arrURLParams.length;i++)
			   {
					var sParam =  arrURLParams[i].split("=");
					arrParamNames[i] = sParam[0];
					if (sParam[1] != ""){
						arrParamValues[i] = unescape(sParam[1]);
					}
					else{
						arrParamValues[i] = "";
					}
			    }

				for (i=0;i<arrURLParams.length;i++)
				{
					if(arrParamNames[i] == paramName){
						return arrParamValues[i];
					}
				}
			}
			return "";
		}
	}
};

Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', TR.conf.finals.ajax.path_lib + 'ext-ux');
Ext.require('Ext.ux.form.MultiSelect');
Ext.require([
    'Ext.ux.grid.FiltersFeature'
]);


Ext.onReady( function() {
    Ext.override(Ext.form.FieldSet,{setExpanded:function(a){var b=this,c=b.checkboxCmp,d=b.toggleCmp,e;a=!!a;if(c){c.setValue(a)}if(d){d.setType(a?"up":"down")}if(a){e="expand";b.removeCls(b.baseCls+"-collapsed")}else{e="collapse";b.addCls(b.baseCls+"-collapsed")}b.collapsed=!a;b.doComponentLayout();b.fireEvent(e,b);return b}});
    Ext.QuickTips.init();
    document.body.oncontextmenu = function(){return false;}; 
    
	var reportId = TR.conf.util.getURLParameters('id');
    Ext.Ajax.request({
        url: TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.initialize + "?id=" + reportId,
		disableCaching: false,
        success: function(r) {
            
    TR.init = TR.conf.init.ajax.jsonfy(r);    
    TR.init.initialize = function() {        
        TR.init.cmd = TR.util.getUrlParam(TR.conf.finals.cmd.urlparam) || TR.conf.finals.cmd.init;
    };
    
    TR.cmp = {
        region: {},
        settings: {},
        params: {
            program:{},
			programStage: {},
			dataelement: {},
			organisationunit: {},
			relativeperiod: {
				checkbox: []
			},
        },
        options: {},
		layoutWindow: {},
        toolbar: {
            menuitem: {}
        },
        statusbar: {},
        caseBasedFavorite: {
            rename: {}
        },
		aggregateFavorite: {
            rename: {}
        }
    };
    
    TR.util = {
		getCmp: function(q) {
            return TR.viewport.query(q)[0];
        },
		list:{
			addOptionToList: function( list, optionValue, optionText ){
				var option = document.createElement( "option" );
				option.value = optionValue;
				option.text = optionText;
				option.setAttribute('selected',true)
				list.add( option, null );
			},
			clearList: function( list ) {
				list.options.length = 0;
			}
		},
        getUrlParam: function(s) {
            var output = '';
            var href = window.location.href;
            if (href.indexOf('?') > -1 ) {
                var query = href.substr(href.indexOf('?') + 1);
                var query = query.split('&');
                for (var i = 0; i < query.length; i++) {
                    if (query[i].indexOf('=') > -1) {
                        var a = query[i].split('=');
                        if (a[0].toLowerCase() === s) {
							output = a[1];
							break;
						}
                    }
                }
            }
            return unescape(output);
        },
        viewport: {
            getSize: function() {
                return {x: TR.cmp.region.center.getWidth(), y: TR.cmp.region.center.getHeight()};
            },
            getXY: function() {
                return {x: TR.cmp.region.center.x + 15, y: TR.cmp.region.center.y + 43};
            },
            getPageCenterX: function(cmp) {
                return ((screen.width/2)-(cmp.width/2));
            },
            getPageCenterY: function(cmp) {
                return ((screen.height/2)-((cmp.height/2)-100));
            },
            resizeParams: function() {
				var a = [TR.cmp.params.dataelement.panel, 
						 TR.cmp.params.organisationunit.treepanel];
				for (var i = 0; i < a.length; i++) {
					if (!a[i].collapsed) {
						a[i].fireEvent('expand');
					}
				}
			}
        },
        multiselect: {
            select: function(a, s, f) {
                var selected = a.getValue();
				if( selected.length > 0 )
				{
					var array = [];
					Ext.Array.each(selected, function(item) {
						var data = a.store.findExact('id', item);
						var name = a.store.getAt(data).data.name;
						var valueType = a.store.getAt(data).data.valueType;
						array.push({id: item, name:name, valueType:valueType});
						if(f!=undefined)
						{
							TR.util.multiselect.addFilterField( f, item, name, valueType );
						}
					});
					s.store.add(array);
					
					this.filterAvailable(a, s);
				}
            },
            selectAll: function(a, s, f) {
				var array = [];
				var elements = a.boundList.all.elements;
				for( var i=0; i< elements.length; i++ )
				{
					if( elements[i].style.display != 'none' )
					{		
						var id = a.store.getAt(i).data.id;
						var name = a.store.getAt(i).data.name;
						var valueType = a.store.getAt(i).data.valueType;
						
						array.push({id: a.store.getAt(i).data.id, name: name, valueType: valueType});
						if(f!=undefined)
						{
							TR.util.multiselect.addFilterField( f, a.store.getAt(i).data.id, name, valueType );
						}
					}
				}
				s.store.add(array);
                this.filterAvailable(a, s);
            },            
            unselect: function(a, s, f) {
                var selected = s.getValue();
				if( selected.length > 0 )
				{
					if (selected.length) {
						Ext.Array.each(selected, function(item) {
							s.store.remove(s.store.getAt(s.store.findExact('id', item)));
						});                    
						this.filterAvailable(a, s);
					}
					if(f!=undefined)
					{
						this.removeFilterField( f, selected[0], a.store.getAt(a.store.findExact('id', selected)).data.valueType );
					}
				}
            },
            unselectAll: function(a, s, f) {
                var elements = s.boundList.all.elements;
				var index = 0;
				var arr = [];
				Ext.Array.each(s.store.data.items, function(item) {
					if( elements[index].style.display != 'none' )
					{
					  arr.push(item.data.id);
					  if(f!=undefined)
					  {
						TR.util.multiselect.removeFilterField( f, item.data.id, item.data.valueType );
					  }
					}
					index++;
				}); 
				s.setValue(arr);
				this.unselect(a,s);
            },
            filterAvailable: function(a, s) {
				a.store.filterBy( function(r) {
                    var filter = true;
                    s.store.each( function(r2) {
                        if (r.data.id == r2.data.id) {
                            filter = false;
                        }
                    });
                    return filter;
                });
            },
			filterSelector: function(selectors, queryString) {
                var elements = selectors.boundList.all.elements;

				for( var i=0; i< elements.length; i++ )
				{
					if( elements[i].innerHTML.toLowerCase().indexOf( queryString ) != -1 )
					{
						elements[i].style.display = 'block';
					}
					else
					{
						elements[i].style.display = 'none';
					}
				}
            },
            setHeight: function(ms, panel, fill) {
				for (var i = 0; i < ms.length; i++) {
					ms[i].setHeight(panel.getHeight() - 49);
				}
			},
			addFilterField: function( p, id, name, valueType, filterValue ){
				var panelid = p + '_' + id;
				var idx = 0;
				var subPanel = Ext.getCmp(panelid);
				if( subPanel == undefined )
				{
					var panel = {
						xtype: 'panel',
						id: panelid,
						layout: 'column',
						bodyStyle: 'border-style:none',
						autoScroll: true,
						overflowX: 'hidden',
						overflowY: 'auto',
						width: TR.conf.layout.west_fieldset_width - 15
					};
					Ext.getCmp(p).add(panel);
					subPanel = Ext.getCmp(panelid);
				}
				else {
					idx = subPanel.items.length/5;
				}
				
				var items = [];
				var fieldid = id + '_' + idx;
				items[0] = {
					xtype: 'label',
					id: 'filter_lb_' + fieldid,
					text:name,
					style: 'padding-left:2px',
					width:(TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2 - 93
				};
				
				var opt = "";
				var filter = "";
				if( filterValue!=undefined && filterValue != "" )
				{
					var arrFilter = filterValue.split(":");
					opt = arrFilter[0];
					filter = arrFilter[1];
				}
				items[1] = this.createOperatorField(valueType, fieldid, opt);
				items[2] = this.createFilterField(valueType, fieldid, filter);
				if( idx == 0 ){
					items[3] = this.addFieldBtn( p, id, name, valueType, idx );
					items[4] = this.addFitlerOptionBox( p, id, name, valueType, idx );
				}
				else
				{
					items[3] = this.removeFieldBtn( panelid, fieldid );
					items[4] = {};
				}
				
				subPanel.add(items);
			},
			removeFilterField: function( p, id ){
				var e1 = Ext.getCmp( p + '_' + id );
				Ext.getCmp(p).remove(e1);
			},
			createOperatorField: function( valueType, id, filterOperator ){
				var params = {};
				params.xtype = 'combobox';
				params.id = 'filter_opt_' + id;
				params.width = 50;
				params.queryMode = 'local';
				params.valueField = 'value';
				params.displayField = 'name';
				params.editable = false;
				params.style = 'margin-bottom:2px';
				valueType = valueType.split('_')[0];
				if( filterOperator != undefined || filterOperator!= '')
				{
					params.value = filterOperator;
				}
				else
				{
					params.value = 'EQ';
				}
				
				if( valueType=='GENDER' )
				{
					params.store = new Ext.data.ArrayStore({
						fields: ['value','name'],
						data: [ ['EQ','='] ]
					});
					params.value = 'EQ';
				}
				else if(valueType == 'string' || valueType == 'list' || valueType == 'username' )
				{
					params.store = new Ext.data.ArrayStore({
						fields: ['value','name'],
						data: [ ['EQ','='],['LIKE',TR.i18n.like],['IN',TR.i18n.in] ]
					});
					params.value = 'IN';
				}
				else if( valueType == 'trueOnly' || valueType == 'bool' ){
					params.store = new Ext.data.ArrayStore({
						fields: ['value','name'],
						data: [ ['EQ','='] ]
					});
					params.value = 'EQ';
				}
				else
				{
					params.store = new Ext.data.ArrayStore({
						fields: ['value','name'],
						data: [ ['EQ','='],
								['GT','>'],
								['GE','>='],
								['LT','<'],
								['LE','<='],
								['NE','!=' ] ]
					});
					params.value = 'EQ';
				}
				
				params.listeners={};
				params.listeners.select = function(cb)  {
					var opt = cb.getValue();
					if(opt == 'in'){
						Ext.getCmp('filter_' + id).multiSelect = true;
					}
					else{
						Ext.getCmp('filter_' + id).multiSelect = false;
					}
				}
				
				return params;
			},
			createFilterField: function( valueType, id, filterValue ){
				var params = {};
				var xtype = TR.value.covertXType(valueType.split('_')[0]);
				params.xtype = xtype;
				params.id = 'filter_' + id;
				params.cls = 'tr-textfield-alt1';
				params.style = 'margin-bottom:2px';
				params.emptyText = TR.i18n.filter_value;
				params.width = (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2 - 50;
				xtype = xtype.toLowerCase();
				if( valueType=='GENDER'){
					params.xtype = 'combobox';
					params.queryMode = 'local';
					params.valueField = 'value';
					params.displayField = 'name';
					params.editable = false;
					params.store = new Ext.data.ArrayStore({
						fields: ['value', 'name'],
						data: [['F', TR.i18n.female], 
							['M', TR.i18n.male], 
							['T', TR.i18n.transgender]]
					});
				}
				else if( xtype == 'datefield' ){
					params.format = TR.i18n.format_date;
				}
				else if( xtype == 'combobox' )
				{
					var deId = id.split('_')[0];
					params.typeAhead = true;
					params.editable = true;
					if( valueType == 'bool' ){
						params.queryMode = 'local';
						params.valueField = 'value';
						params.displayField = 'name';
						params.editable = false;
						params.forceSelection = true;
						params.store = new Ext.data.ArrayStore({
							fields: ['value', 'name'],
							data: [['', TR.i18n.please_select], 
								['true', TR.i18n.yes], 
								['false', TR.i18n.no]]
						});
					}
					else if( valueType == 'trueOnly'){
						params.queryMode = 'local';
						params.valueField = 'value';
						params.displayField = 'name';
						params.editable = false;
						params.store = new Ext.data.ArrayStore({
							fields: ['value', 'name'],
							data: [['', TR.i18n.please_select],['true', TR.i18n.yes]]
						});
					}
					else if(valueType=='username'){
						params.queryMode = 'remote';
						params.valueField = 'u';
						params.displayField = 'u';
						params.multiSelect = true;
						params.delimiter = ';';
						params.store = Ext.create('Ext.data.Store', {
							fields: ['u'],
							data:[],
							proxy: {
								type: 'ajax',
								url: TR.conf.finals.ajax.path_commons + TR.conf.finals.ajax.username_dataelement_get,
								reader: {
									type: 'json',
									root: 'usernames'
								}
							}
						});
					}
					else{
						params.queryMode = 'remote';
						params.valueField = 'o';
						params.displayField = 'o';
						params.multiSelect = true;
						params.delimiter = ';';
						params.store = Ext.create('Ext.data.Store', {
							fields: ['o'],
							data:[],
							proxy: {
								type: 'ajax',
								url: TR.conf.finals.ajax.path_commons + TR.conf.finals.ajax.suggested_dataelement_get,
								extraParams:{id: valueType.split('_')[1]},
								reader: {
									type: 'json',
									root: 'options'
								}
							}
						});
					}					
				}
				
				if( filterValue != undefined )
				{
					params.value = filterValue;
				}
				else
				{
					params.value = '';
				}
				return params;
			},
			addFieldBtn: function( p, id, name, valueType, idx ){
				var params = {};
				params.xtype = 'button';
				params.text = "+";
				params.style = 'margin-bottom:2px';
				params.tooltip = TR.i18n.add,
				params.handler = function() {
					TR.util.multiselect.addFilterField(p, id, name, valueType);
				}
				
				return params;
			},
			removeFieldBtn: function( p, id ){
				var params = {};
				params.xtype = 'button';
				params.id = 'filter_rmv_' + id;	
				params.text = "-";
				params.style = 'margin-bottom:2px';
				params.tooltip = TR.i18n.remove,
				params.handler = function() {
					var e1 = Ext.getCmp( 'filter_' + id );
					var e2 = Ext.getCmp( 'filter_opt_' + id );	
					var e3 = Ext.getCmp( 'filter_lb_' + id );
					var e4 = Ext.getCmp( 'filter_rmv_' + id );
					Ext.getCmp(p).remove(e1);
					Ext.getCmp(p).remove(e2);
					Ext.getCmp(p).remove(e3);
					Ext.getCmp(p).remove(e4);
				}
				return params;
			},
			addFitlerOptionBox: function( p, id ){
				var params = {};
				params.xtype = 'combobox';
				params.id = 'filter_dimension_' + id;
				params.width = 75;
				params.queryMode = 'local';
				params.valueField = 'value';
				params.displayField = 'name';
				params.style = 'margin-bottom:2px';
				params.editable = false;
				params.store = new Ext.data.ArrayStore({
						fields: ['value','name'],
						data: [ ['dimension',TR.i18n.dimension],['filter',TR.i18n.filter] ]
					});
				params.value = 'dimension';
				
				return params;
			}
        },
        positionFilter:{
			orgunit: function() {
				var o = TR.cmp.settings.positionOrgunit.value;
				
				// Orgunit is columns
				if( o==1 )
				{
					var periodStore = TR.cmp.settings.positionPeriod.store;
					periodStore.removeAll();
					periodStore.add (
						{value: 1,name: TR.i18n.rows},
						{value: 2,name: TR.i18n.columns},
						{value: 3,name: TR.i18n.filters}
					);
					Ext.getCmp('positionPeriodCbx').setValue( 1 );
				}
				// Orgunit is columns
				else if( o==2 )
				{
					var periodStore = TR.cmp.settings.positionPeriod.store;
					periodStore.removeAll();
					periodStore.add (
						{value: 1,name: TR.i18n.rows},
						{value: 3,name: TR.i18n.filter}
					);
					Ext.getCmp('positionPeriodCbx').setValue( 1 );
					
					var dataStore = TR.cmp.settings.positionData.store;
					dataStore.removeAll();
					dataStore.add ({value: 3,name: TR.i18n.filters});
					Ext.getCmp('positionDataCbx').setValue( 3 );
				}
				// Orgunit is filters
				else if( o==3)
				{
					var periodStore = TR.cmp.settings.positionPeriod.store;
					periodStore.removeAll();
					periodStore.add (
						{value: 1,name: TR.i18n.rows},
						{value: 2,name: TR.i18n.columns},
						{value: 3,name: TR.i18n.filters}
					);
					Ext.getCmp('positionPeriodCbx').setValue( 1 );
					
					var dataStore = TR.cmp.settings.positionData.store;
					dataStore.removeAll();
					dataStore.add (
						{value: 2,name: TR.i18n.columns},
						{value: 3,name: TR.i18n.filters}
					);
					Ext.getCmp('positionDataCbx').setValue( 2 );
				}
			},
			period: function(){
				// Orgunit is column
				var o = TR.cmp.settings.positionOrgunit.value;
				var p = TR.cmp.settings.positionPeriod.value;
				
				var dataStore = TR.cmp.settings.positionData.store;
				dataStore.removeAll();
					
				if( o==1 ){
					if( p==1 || p==2 ){
						dataStore.add (
							{value: 3,name: TR.i18n.filters}
						);
					}
					else if( p==3 ){
						dataStore.add (
							{value: 2,name: TR.i18n.columns},
							{value: 3,name: TR.i18n.filters}
						);
					}
					Ext.getCmp('positionDataCbx').setValue( 3 );
				}
				else if( o==2 ){
					if( p==3 ){
						dataStore.add (
							{value: 1,name: TR.i18n.rows}
						);
						Ext.getCmp('positionDataCbx').setValue( 1 );
					}
					else if( p==1 ){
						dataStore.add (
							{value: 3,name: TR.i18n.filters}
						);
						Ext.getCmp('positionDataCbx').setValue( 3 );
					}
				}
				else if( o==3 && p==1 ){
					dataStore.add (
						{value: 2,name: TR.i18n.columns},
						{value: 3,name: TR.i18n.filters}
					);
					Ext.getCmp('positionDataCbx').setValue( 2 );
				}
				else if( o==3 && ( p==2 || p==3 ) ){
					var dataStore = TR.cmp.settings.positionData.store;
					dataStore.removeAll();
					dataStore.add (
						{value: 1,name: TR.i18n.rows}
					);
					Ext.getCmp('positionDataCbx').setValue( 1 );
				}
			},
			convert: function( position )
			{
				Ext.getCmp('positionRowCbx').store.removeAll();
				Ext.getCmp('positionColCbx').store.removeAll();
				Ext.getCmp('positionFilterCbx').store.removeAll();
				
				switch( eval(position) ){
				
				case TR.conf.reportPosition.POSITION_ROW_ORGUNIT_COLUMN_PERIOD :
					Ext.getCmp('positionRowCbx').store.add({id:1, name:TR.i18n.organisation_units});
					Ext.getCmp('positionColCbx').store.add({id:2, name:TR.i18n.periods});
					Ext.getCmp('positionFilterCbx').store.add({id:3, name:TR.i18n.data});
					break;
				case TR.conf.reportPosition.POSITION_ROW_PERIOD_COLUMN_ORGUNIT :
					Ext.getCmp('positionRowCbx').store.add({id:2, name:TR.i18n.periods});
					Ext.getCmp('positionColCbx').store.add({id:1, name:TR.i18n.organisation_units});
					Ext.getCmp('positionFilterCbx').store.add({id:3, name:TR.i18n.data});
					break;
				case TR.conf.reportPosition.POSITION_ROW_ORGUNIT_ROW_PERIOD :
					Ext.getCmp('positionRowCbx').store.add({id:1, name:TR.i18n.organisation_units});
					Ext.getCmp('positionRowCbx').store.add({id:2, name:TR.i18n.periods});
					Ext.getCmp('positionFilterCbx').store.add({id:3, name:TR.i18n.data});
					break;
				case TR.conf.reportPosition.POSITION_ROW_PERIOD :
					Ext.getCmp('positionRowCbx').store.add({id:2, name:TR.i18n.periods});
					Ext.getCmp('positionFilterCbx').store.add({id:1, name:TR.i18n.organisation_units});
					Ext.getCmp('positionFilterCbx').store.add({id:3, name:TR.i18n.data});
					break;
				case TR.conf.reportPosition.POSITION_ROW_PERIOD_COLUMN_DATA :
					Ext.getCmp('positionRowCbx').store.add({id:2, name:TR.i18n.periods});
					Ext.getCmp('positionColCbx').store.add({id:3, name:TR.i18n.data});
					Ext.getCmp('positionFilterCbx').store.add({id:1, name:TR.i18n.organisation_units});
					break;
				case TR.conf.reportPosition.POSITION_ROW_ORGUNIT :
					Ext.getCmp('positionRowCbx').store.add({id:1, name:TR.i18n.organisation_units});
					Ext.getCmp('positionFilterCbx').store.add({id:2, name:TR.i18n.periods});
					Ext.getCmp('positionFilterCbx').store.add({id:3, name:TR.i18n.data});
					break;	
				case TR.conf.reportPosition.POSITION_ROW_ORGUNIT_COLUMN_DATA :
					Ext.getCmp('positionRowCbx').store.add({id:1, name:TR.i18n.organisation_units});
					Ext.getCmp('positionColCbx').store.add({id:3, name:TR.i18n.data});
					Ext.getCmp('positionFilterCbx').store.add({id:2, name:TR.i18n.periods});
					break;
				case TR.conf.reportPosition.POSITION_ROW_DATA :
					Ext.getCmp('positionRowCbx').store.add({id:3, name:TR.i18n.data});
					Ext.getCmp('positionFilterCbx').store.add({id:2, name:TR.i18n.periods});
					Ext.getCmp('positionFilterCbx').store.add({id:1, name:TR.i18n.organisation_units});
					break;
				case TR.conf.reportPosition.POSITION_ROW_DATA_COLUMN_PERIOD :
					Ext.getCmp('positionRowCbx').store.add({id:3, name:TR.i18n.data});
					Ext.getCmp('positionColCbx').store.add({id:2, name:TR.i18n.periods});
					Ext.getCmp('positionFilterCbx').store.add({id:1, name:TR.i18n.organisation_units});
					break;
				case TR.conf.reportPosition.POSITION_ROW_DATA_COLUMN_ORGUNIT :
					Ext.getCmp('positionRowCbx').store.add({id:3, name:TR.i18n.data});
					Ext.getCmp('positionColCbx').store.add({id:1, name:TR.i18n.organisation_units});
					Ext.getCmp('positionFilterCbx').store.add({id:2, name:TR.i18n.periods});
					break;
				}
			}
		},
		store: {
            addToStorage: function(s, records) {
                s.each( function(r) {
                    if (!s.storage[r.data.id]) {
                        s.storage[r.data.id] = {id: r.data.id, name: TR.util.string.getEncodedString(r.data.name), parent: s.parent, valueType: r.data.valueType};
                    }
                });
                if (records) {
                    Ext.Array.each(records, function(r) {
                        if (!s.storage[r.data.id]) {
                            s.storage[r.data.id] = {id: r.data.id, name: TR.util.string.getEncodedString(r.data.name), parent: s.parent};
                        }
                    });
                }                        
            },
            loadFromStorage: function(s) {
                var items = [];
                s.removeAll();
                for (var obj in s.storage) {
                    if (s.storage[obj].parent === s.parent) {
                        items.push(s.storage[obj]);
                    }
                }
                s.add(items);
                s.sort('name', 'ASC');
            },
            containsParent: function(s) {
                for (var obj in s.storage) {
                    if (s.storage[obj].parent === s.parent) {
                        return true;
                    }
                }
                return false;
            }
        },
        notification: {
			error: function(title, text) {
				title = title || '';
				text = text || '';
				Ext.create('Ext.window.Window', {
					title: title,
					cls: 'tr-messagebox',
					iconCls: 'tr-window-title-messagebox',
					modal: true,
					width: 300,
					items: [
						{
							xtype: 'label',
							width: 40,
							text: text
						}
					]
				}).show();
				TR.cmp.statusbar.panel.setWidth(TR.cmp.region.center.getWidth());
				TR.cmp.statusbar.panel.update('<img src="' + TR.conf.finals.ajax.path_images + TR.conf.statusbar.icon.error + '" style="padding:0 5px 0 0"/>' + text);
			},
			warning: function(text) {
				text = text || '';
				TR.cmp.statusbar.panel.setWidth(TR.cmp.region.center.getWidth());
				TR.cmp.statusbar.panel.update('<img src="' + TR.conf.finals.ajax.path_images + TR.conf.statusbar.icon.warning + '" style="padding:0 5px 0 0"/>' + text);
			},
			ok: function() {
				TR.cmp.statusbar.panel.setWidth(TR.cmp.region.center.getWidth());
				TR.cmp.statusbar.panel.update('<img src="' + TR.conf.finals.ajax.path_images + TR.conf.statusbar.icon.ok + '" style="padding:0 5px 0 0"/>&nbsp;&nbsp;');
			}				
		},
        mask: {
            showMask: function(cmp, str) {
                if (TR.mask) {
                    TR.mask.destroy();
                }
                TR.mask = new Ext.LoadMask(cmp, {msg: str});
                TR.mask.show();
            },
            hideMask: function() {
				if (TR.mask) {
					TR.mask.hide();
				}
			}
        },
		/*FIXME:This is probably not going to work as intended with UNICODE?*/
        string: {
            getEncodedString: function(text) {
                return text.replace(/[^a-zA-Z 0-9(){}<>_!+;:?*&%#-]+/g,'');
            }
        },
		getValueFormula: function( value ){
			if( value.indexOf('"') != value.lastIndexOf('"') )
			{
				value = value.replace(/"/g,"'");
			}
			// if key is [xyz] && [=xyz]
			if( value.indexOf("'")==-1 ){
				var flag = value.match(/[>|>=|<|<=|=|!=]+[ ]*/);
			
				if( flag == null )
				{
					value = "='"+ value + "'";
				}
				else
				{
					value = value.replace( flag, flag + "'");
					value +=  "'";
				}
			}
			// if key is ['xyz'] && [='xyz']
			// if( value.indexOf("'") != value.lastIndexOf("'") )
			else
			{
				var flag = value.match(/[>|>=|<|<=|=|!=]+[ ]*/);
			
				if( flag == null )
				{
					value = "="+ value;
				}
			}
			return value;
		},
        crud: {
            favorite: {
                create: function(fn, isupdate) {
					var url = "";
					if(Ext.getCmp('reportTypeGroup').getValue().reportType=='true')
					{
						this.caseBasedReport.create(fn, isupdate);
					}
					else
					{
						this.aggregateReport.create(fn, isupdate);
					}
                },
                update: function(fn) {
					TR.util.crud.favorite.create(fn, true);
                },
				updateName: function(id, name) {
                    if(Ext.getCmp('reportTypeGroup').getValue().reportType=='true')
					{
						this.caseBasedReport.updateName(id, name);
					}
					else
					{
						this.aggregateReport.updateName(id, name);
					}
                },
                del: function(fn) {
					if(Ext.getCmp('reportTypeGroup').getValue().reportType=='true')
					{
						this.caseBasedReport.del(fn);
					}
					else
					{
						this.aggregateReport.del(fn);
					}
                },
				run: function(id) {
					TR.state.asc = "";
					TR.state.desc = "";
					if(Ext.getCmp('reportTypeGroup').getValue().reportType=='true')
					{
						this.caseBasedReport.run( id );
					}
					else
					{
						this.aggregateReport.run( id );
					}
				},			
				
				caseBasedReport:{
					updateName: function(id, name) {
						if (TR.store.caseBasedFavorite.findExact('name', name) != -1) {
							return;
						}
						TR.util.mask.showMask(TR.cmp.caseBasedFavorite.window, TR.i18n.renaming + '...');
						Ext.Ajax.request({
							url:  TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.casebasedfavorite_rename,
							disableCaching: false,
							method: 'POST',
							params: {id: id, name: name},
							success: function() {
								TR.store.caseBasedFavorite.load({callback: function() {
									TR.cmp.caseBasedFavorite.rename.window.close();
									TR.util.mask.hideMask();
									TR.cmp.caseBasedFavorite.grid.getSelectionModel().select(TR.store.caseBasedFavorite.getAt(TR.store.caseBasedFavorite.findExact('name', name)));
									TR.cmp.caseBasedFavorite.name.setValue(name);
								}});
							}
						});
					},
					del: function(fn) {
						TR.util.mask.showMask(TR.cmp.caseBasedFavorite.window, TR.i18n.deleting + '...');
						var id = TR.cmp.caseBasedFavorite.grid.getSelectionModel().getSelection()[0].data.id;
						var baseurl =  TR.conf.finals.ajax.casebasedfavorite_delete + "?id=" + id;
						selection = TR.cmp.caseBasedFavorite.grid.getSelectionModel().getSelection();
						Ext.Array.each(selection, function(item) {
							baseurl = Ext.String.urlAppend(baseurl, 'uids=' + item.data.id);
						});
						
						Ext.Ajax.request({
							url: baseurl,
							disableCaching: false,
							method: 'POST',
							success: function() {
								TR.store.caseBasedFavorite.load({callback: function() {
									TR.util.mask.hideMask();
									if (fn) {
										fn();
									}
								}});
							}
						}); 
					},
					run: function(id) {
						Ext.Ajax.request({
							url: TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.casebasedfavorite_get + '?id=' + id,
							disableCaching: false,
							scope: this,
							success: function(r) {
								var f = Ext.JSON.decode(r.responseText);
								
								Ext.getCmp('startDate').setValue( f.startDate );
								Ext.getCmp('endDate').setValue( f.endDate );
								
								Ext.getCmp('programCombobox').setValue( f.programId );
								TR.store.programStage.parent = f.programStageId;
								TR.store.programStage.isLoadFromFavorite = true;
								TR.store.programStage.removeAll();
								for (var i = 0; i < f.programStages.length; i++)
								{
									TR.store.programStage.add(
										{'id': f.programStages[i].id,'name': f.programStages[i].name}
									);
								}
								Ext.getCmp('programStageCombobox').setValue( f.programStageId );
								Ext.getCmp('ouModeCombobox').setValue( f.ouMode );
								
								// Orgunits
								
								if( f.userOrgunits!=""){
									Ext.getCmp('userOrgunit').setValue(true);
								}
								else if( f.userOrgunitChildren!=""){
									Ext.getCmp('userOrgunitChildren').setValue(true)
								}
								
								var treepanel = TR.cmp.params.organisationunit.treepanel;
								treepanel.getSelectionModel().deselectAll();
								TR.state.orgunitIds = [];
								for (var i = 0; i < f.orgunitIds.length; i++) {
									TR.state.orgunitIds.push( f.orgunitIds[i].id );
								}
								treepanel.selectByIds(TR.state.orgunitIds);
								
								// Data element
								
								Ext.getCmp('filterPanel').removeAll();
								Ext.getCmp('filterPanel').doLayout();
	
								TR.store.dataelement.selected.removeAll();
								if (f.items) {
									for (var i = 0; i < f.items.length; i++) {
										var filter = f.items[i].id.split(';');
										var id = filter[0];
										var name = TR.util.string.getEncodedString(f.items[i].name);
										var valueType = f.items[i].valueType;
										TR.store.dataelement.selected.add({id: id, name: name, valueType: valueType});
										TR.util.multiselect.addFilterField( 'filterPanel', id, name, valueType, f.filters[id] );
										var dimension = f.items[i].dimension;
										if(dimension=='false'){
											Ext.getCmp('filter_dimension_' + id).setValue( 'filter' );
										}
									}
									
									if( f.singleEvent == 'false' )
									{
										var store = TR.store.dataelement.available;
										store.parent = f.programStageId;
										if (TR.util.store.containsParent(store)) {
											TR.util.store.loadFromStorage(store);
											TR.util.multiselect.filterAvailable(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected);
											var dimension = f.items[i].dimension;
											if(dimension=='false'){
												Ext.getCmp('filter_dimension_' + id).setValue( 'filter' );
											}
										}
										else {
											store.load({params: {programStageId: f.programStageId}});
										}
									}
								}
								
								TR.exe.execute();
							}
						});
					}				
				},
				
				aggregateReport:{
					updateName: function(id, name) {
						TR.util.mask.showMask(TR.cmp.aggregateFavorite.window, TR.i18n.renaming + '...');
						
						Ext.Ajax.request({
							url: TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.aggregatefavorite_validate,
							disableCaching: false,
							method: 'POST',
							params: {id:id, name:name},
							success: function(r) {
									var json = Ext.JSON.decode(r.responseText);
									if(json.response=='success'){
										Ext.Ajax.request({
											url: TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.aggregatefavorite_rename,
											disableCaching: false,
											method: 'POST',
											params: {id: id, name: name},
											success: function() {
												TR.store.aggregateFavorite.load({callback: function() {
													TR.cmp.aggregateFavorite.rename.window.close();
													TR.util.mask.hideMask();
													TR.cmp.aggregateFavorite.grid.getSelectionModel().select(TR.store.aggregateFavorite.getAt(TR.store.aggregateFavorite.findExact('name', name)));
													TR.cmp.aggregateFavorite.name.setValue(name);
												}});
											}
										});  
									}
									else{
										TR.util.notification.error(TR.i18n.error, json.message);
										window.destroy();
										TR.util.mask.hideMask();
									}
								}
							});
					},
					del: function(fn) {
						TR.util.mask.showMask(TR.cmp.aggregateFavorite.window, TR.i18n.deleting + '...');
						var id = TR.cmp.aggregateFavorite.grid.getSelectionModel().getSelection()[0].data.id;
						var baseurl =  TR.conf.finals.ajax.aggregatefavorite_delete + "?id=" + id;
							selection = TR.cmp.aggregateFavorite.grid.getSelectionModel().getSelection();
						Ext.Array.each(selection, function(item) {
							baseurl = Ext.String.urlAppend(baseurl, 'uids=' + item.data.id);
						});
						Ext.Ajax.request({
							url: baseurl,
							disableCaching: false,
							method: 'POST',
							success: function() {
								TR.store.aggregateFavorite.load({callback: function() {
									TR.util.mask.hideMask();
									if (fn) {
										fn();
									}
								}});
							}
						}); 
					},	
					run: function(id) {
						Ext.Ajax.request({
							url: TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.aggregatefavorite_get + '?id=' + id,
							disableCaching: false,
							scope: this,
							success: function(r) {
								var f = Ext.JSON.decode(r.responseText);
								
								Ext.getCmp('startDate').setValue( f.startDate );
								Ext.getCmp('endDate').setValue( f.endDate );
								
								Ext.getCmp('programCombobox').setValue( f.programId );
								TR.store.programStage.parent = f.programStageId;
								TR.store.programStage.isLoadFromFavorite = true;
								TR.store.programStage.removeAll();
								for (var i = 0; i < f.programStages.length; i++)
								{
									TR.store.programStage.add(
										{'id': f.programStages[i].id,'name': f.programStages[i].name}
									);
								}
								Ext.getCmp('programStageCombobox').setValue( f.programStageId );
								Ext.getCmp('ouModeCombobox').setValue( f.ouMode );
								
								// Orgunits
								
								if( f.userOrgunits!=""){
									Ext.getCmp('userOrgunit').setValue(true);
								}
								else if( f.userOrgunitChildren!=""){
									Ext.getCmp('userOrgunitChildren').setValue(true)
								}
								
								var treepanel = TR.cmp.params.organisationunit.treepanel;
								treepanel.getSelectionModel().deselectAll();
								TR.state.orgunitIds = [];
								for (var i = 0; i < f.orgunitIds.length; i++) {
									TR.state.orgunitIds.push( f.orgunitIds[i].id );
								}
								treepanel.selectByIds(TR.state.orgunitIds);
								
								// Data element
								
								Ext.getCmp('filterPanel').removeAll();
								Ext.getCmp('filterPanel').doLayout();
	
								TR.store.dataelement.selected.removeAll();
								if (f.items) {
									for (var i = 0; i < f.items.length; i++) {
										var filter = f.items[i].id.split(';');
										var id = filter[0];
										var name = TR.util.string.getEncodedString(f.items[i].name);
										var valueType = f.items[i].valueType;
										TR.store.dataelement.selected.add({id: id, name: name, valueType: valueType});
										
										TR.util.multiselect.addFilterField( 'filterPanel', id, name, valueType, f.filters[id] );
										var dimension = f.items[i].dimension;
										if(dimension=='false'){
											Ext.getCmp('filter_dimension_' + id).setValue( 'filter' );
										}
									}
									
									if( f.singleEvent == 'false' )
									{
										var store = TR.store.dataelement.available;
										store.parent = f.programStageId;
										if (TR.util.store.containsParent(store)) {
											TR.util.store.loadFromStorage(store);
											TR.util.multiselect.filterAvailable(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected);
											var dimension = f.items[i].dimension;
											if(dimension=='false'){
												Ext.getCmp('filter_dimension_' + id).setValue( 'filter' );
											}
										}
										else {
											store.load({params: {programStageId: f.programStageId}});
										}
									}
								}
								
								TR.exe.execute();
							}
						});
					}				
				}
		   }
        },
		window: {
			setAnchorPosition: function(w, target) {
				var vpw = 1000,
					targetx = target ? target.getPosition()[0] : 600,
					winw = w.getWidth(),
					y = target ? target.getPosition()[1] + target.getHeight() + 4 : 33;

				if ((targetx + winw) > vpw) {
					w.setPosition((vpw - winw - 2), y);
				}
				else {
					w.setPosition(targetx, y);
				}
			}
		}
	};
    
    TR.store = {
		program: Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'type', 'localid'],
			data:TR.init.system.program
		}),
		orgunitGroup: Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'type'],
			data:TR.init.system.orgunitGroup,
			listeners: {
				load: function() {
					this.insert(0,{id:"", name: TR.i18n.none});
				}
			}
		}),
		programStage: Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: TR.conf.finals.ajax.path_api + TR.conf.finals.ajax.programstages_get,
				reader: {
					type: 'json',
					root: 'programStages'
				}
			},
			isLoadFromFavorite: false,
			listeners:{
				load: function(s) {
					this.insert(0,{id:"", name: TR.i18n.all});
					Ext.override(Ext.LoadMask, {
						 onHide: function() {
							  this.callParent();
						 }
					});
					
					if( TR.store.programStage.data.items.length > 1 )
					{
						Ext.getCmp('programStageCombobox').enable();
						Ext.getCmp('programStageCombobox').setValue( "" );
					}
					else
					{
						var programStageId = TR.store.programStage.data.items[0].raw.id;
						
						Ext.getCmp('programStageCombobox').disable();
						Ext.getCmp('programStageCombobox').setValue( TR.store.programStage.data.items[0].raw.id );
						
						// Load sections if any
						
						var store = TR.store.programStageSection;
						TR.store.programStageSection.loadData([],false);
						if( !TR.store.programStageSection.isLoad )
						{
							store.loadData([],false);
						}
						store.parent = programStageId;
						
						if (TR.util.store.containsParent(store)) {
							TR.util.store.loadFromStorage(store);
						}
						else {
							store.load({params: {programStageId: programStageId}});
						}
						
						// Load data element list
						
						store = TR.store.dataelement.available;
						TR.store.dataelement.available.loadData([],false);
						if( !TR.store.programStage.isLoadFromFavorite)
						{
							TR.store.dataelement.selected.loadData([],false);
						}
						store.parent = programStageId;
						
						if (TR.util.store.containsParent(store)) {
							TR.util.store.loadFromStorage(store);
							TR.util.multiselect.filterAvailable(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected);
						}
						else {
							store.load({params: {programStageId: programStageId}});
						}
					}
				}
			}
		}),
		programStageSection: Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: TR.conf.finals.ajax.path_commons + TR.conf.finals.ajax.programstagesections_get,
				reader: {
					type: 'json',
					root: 'sections'
				}
			},
			isLoad: false,
			listeners:{
				load: function(s) {
					Ext.override(Ext.LoadMask, {
						 onHide: function() {
							  this.callParent();
						 }
					});
					
					if( TR.store.programStageSection.data.items.length == 0 ){
						Ext.getCmp('sectionCombobox').setVisible(false);
					}
					else{
						Ext.getCmp('sectionCombobox').setVisible(true);
						TR.store.programStageSection.insert(0, {
							'id': "",
							'name': TR.i18n.please_select
						});
					}
					Ext.getCmp('sectionCombobox').setValue( "" );
				}
			}
		}),
		dataelement: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 'name', 'valueType'],
                proxy: {
                    type: 'ajax',
                    url: TR.conf.finals.ajax.path_commons + TR.conf.finals.ajax.dataelements_get,
					disableCaching: false,
                    reader: {
                        type: 'json',
                        root: 'items'
                    }
                },
                isloaded: false,
                storage: {},
                listeners: {
					load: function(s) {
						this.isloaded = true;
                        TR.util.store.addToStorage(s);
                        TR.util.multiselect.filterAvailable(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected);
						
						TR.store.aggregateDataelement.loadData([],false);
						TR.cmp.params.dataelement.available.store.each( function(r) {
							if(r.data.valueType == 'int'){
								TR.store.aggregateDataelement.add({
									'id': r.data.id,
									'name': r.data.name
								});
							}
						});
						
                    }
				}
            }),
            selected: Ext.create('Ext.data.Store', {
                fields: ['id', 'name', 'valueType'],
                data: []
            })
        },
        datatable: null,
        getDataTableStore: function() {
			this.datatable = Ext.create('Ext.data.ArrayStore', {
				fields: TR.value.fields,
				data: TR.value.values,
				sorters: [{
					property: 'value',
					direction: 'ASC'
				}]
			});
        },
		caseBasedFavorite: Ext.create('Ext.data.Store', {
			fields: ['id', 'localid', 'name', 'lastUpdated', 'access'],
			proxy: {
				type: 'ajax',
				reader: {
					type: 'json',
					root: 'tabularReports'
				}
			},
			isloaded: false,
            pageSize: 10,
            currentPage: 1,
			defaultUrl: TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.casebasedfavorite_getall,
			loadStore: function(url) {
				this.proxy.url = url || this.defaultUrl;
				this.load({
					params: {
						pageSize: this.pageSize,
						currentPage: this.currentPage
					}
				});
			},
			loadFn: function(fn) {
				if (this.isLoaded) {
					fn.call();
				}
				else {
					this.load(fn);
				}
			},
			sorting: {
                field: 'name',
                direction: 'ASC'
            },
            sortStore: function() {
                this.sort(this.sorting.field, this.sorting.direction);
            },
            filtersystem: function() {
				if (!TR.init.user.isAdmin) {
					this.filterBy( function(r) {
						return r.data.userId ? true : false;
					});
				}
			},
			listeners: {
				load: function(s) {
					s.isloaded = !s.isloaded ? true : false;
					
					s.sortStore();
					s.each(function(r) {
						r.data.lastUpdated = r.data.lastUpdated.substr(0,16);
						r.data.icon = '<img src="' + TR.conf.finals.ajax.path_images + 'favorite.png" />';
						r.commit();
					});
				}
            }
		}),
		aggregateFavorite: Ext.create('Ext.data.Store', {
			fields: ['id', 'localid', 'name', 'lastUpdated', 'access'],
			proxy: {
				type: 'ajax',
				reader: {
					type: 'json',
					root: 'tabularReports'
				}
			},
			isloaded: false,
			pageSize: 10,
            currentPage: 1,
			defaultUrl: TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.aggregatefavorite_getall,
			loadStore: function(url) {
				this.proxy.url = url || this.defaultUrl;
				this.load({
					params: {
						pageSize: this.pageSize,
						currentPage: this.currentPage
					}
				});
			},
			loadFn: function(fn) {
				if (this.isLoaded) {
					fn.call();
				}
				else {
					this.load(fn);
				}
			},
			sorting: {
                field: 'name',
                direction: 'ASC'
            },
            sortStore: function() {
                this.sort(this.sorting.field, this.sorting.direction);
            },
            filtersystem: function() {
				if (!TR.init.user.isAdmin) {
					this.filterBy( function(r) {
						return r.data.userId ? true : false;
					});
				}
			},
			listeners: {
                load: function(s) {
					s.isloaded = !s.isloaded ? true : false;
					
                    s.sortStore();
                    s.each(function(r) {
                        r.data.lastUpdated = r.data.lastUpdated.substr(0,16);
                        r.data.icon = '<img src="' + TR.conf.finals.ajax.path_images + 'favorite.png" />';
                        r.commit();
                    });
                }
            }
		}),
		aggregateDataelement: Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: []
		})
	}
    
    TR.state = {
        currentPage: 1,
		pageCount: 1,
		total: 0,
		asc: "",
		desc: "",
		sortOrder: "ASC",
		orgunitIds: [],
		generateReport: function( type ) {
			if(Ext.getCmp('reportTypeGroup').getValue().reportType=='true')
			{
				this.caseBasedReport.generate( type );
			}
			else
			{
				this.aggregateReport.generate( type );
			}
		},
		getParams: function(){
			
			var p = {};
			
			// Start-date && End-date
			p.startDate = TR.cmp.settings.startDate.rawValue;
			p.endDate = TR.cmp.settings.endDate.rawValue;
			
			if( TR.cmp.settings.ouMode.getValue()!== null ||  TR.cmp.settings.ouMode.getValue()!='' ){
				p.ouMode = TR.cmp.settings.ouMode.getValue();
			}
			
			// Get searching values
			
			p.dimension = [];
			p.filter = [];
			
			// User orgunits
			
			if( Ext.getCmp('userOrgunit').getValue() === true ){
				p.dimension.push( "ou:" + TR.conf.userOrgunit.USER_ORGUNIT );
			}
			if( Ext.getCmp('userOrgunitChildren').getValue() === true ){
				p.dimension.push( "ou:" + TR.conf.userOrgunit.USER_ORGUNIT_CHILDREN );
			}
			
			// Selected orgunits
			
			if( Ext.getCmp('userOrgunit').getValue() === false 
				&& Ext.getCmp('userOrgunitChildren').getValue() === false){
				var ou = "ou:";
				for( var i in TR.state.orgunitIds){
					ou += TR.state.orgunitIds[i];
					if( i<TR.state.orgunitIds.length - 1 ){
						ou +=";"
					}
				}
				p.dimension.push( ou );
			}
			
			// Filter
			
			var idx = 0;
			TR.cmp.params.dataelement.selected.store.each( function(r) {
				var valueType = r.data.valueType;
				var deId = r.data.id;
				var length = Ext.getCmp('filterPanel_' + deId).items.length/5;
				var hidden = TR.state.caseBasedReport.isColHidden(deId);
				var dimensionOption = 'dimension';
				
				for(var idx=0;idx<length;idx++)
				{
					var id = deId + '_' + idx;
					if(Ext.getCmp('filter_' + id)!=undefined)
					{
						if( idx==0 )
						{
							dimensionOption = Ext.getCmp('filter_dimension_' + deId ).getValue();
						}
						
						var filterOpt = Ext.getCmp('filter_opt_' + id).getValue();
						var filterValue = Ext.getCmp('filter_' + id).rawValue.toLowerCase();;
						if(deId=='GENDER' || Ext.getCmp('filter_' + id).xtype=='combobox'){	
							filterValue = Ext.getCmp('filter_' + id).getValue();
						}					
						var filter = deId;
						if( Ext.getCmp('filter_' + id).getValue()!=null 
							&& Ext.getCmp('filter_' + id).getValue()!=''){
							
							filterValue = filterValue
							filter += ':' + filterOpt + ':';
							if( filterOpt == 'IN' )
							{
								filter += filterValue.replace(/:/g,";"); 
							}
							else
							{
								filter += filterValue;
							}
						}
						
						if( dimensionOption=='dimension' ){
							p.dimension.push( filter );
						}
						else{
							p.filter.push( filter );
						}
					}
				}
			});
					
			var reportType = Ext.getCmp('reportTypeGroup').getValue().reportType;
			if(reportType=='true')
			{
				// Order-by
				if(TR.state.asc!=""){				
					p.asc = TR.state.asc;
				}
				else if(TR.state.desc!=""){
					p.desc= TR.state.desc;
				}
			}
			else{
				if(Ext.getCmp('limitOption').getValue()!==null){
					p.limit = Ext.getCmp('limitOption').getValue();
				}
				
				// Relative periods
				var rperiod = TR.cmp.params.relativeperiod.checkbox;
				var period = '';
				Ext.Array.each(rperiod, function(item) {
					if(item.getValue() && !item.hidden){
						period += item.paramName + ";";
					}
				});
				if( period.length > 0 ){
					period = period.substr(0,period.length-1);
					p.dimension.push('pe:' + period);
				}
				
				// Sort-order
				if( TR.state.sortOrder!= '' ){
					p.sortOrder = TR.state.sortOrder
				}
			}
			
			return p;
		},
		getURLParams: function( type ){
			var params = "";
			
			params += '&startDate=' + TR.cmp.settings.startDate.rawValue;
			params += '&endDate=' + TR.cmp.settings.endDate.rawValue;
			if(TR.cmp.settings.ouMode != null && TR.cmp.settings.ouMode.getValue()!="" ){
				params += '&ouMode=' + TR.cmp.settings.ouMode.getValue();
			}
			
			// User orgunits
			
			if( Ext.getCmp('userOrgunit').getValue() === true ){
				params += '&dimension=ou:' + TR.conf.userOrgunit.USER_ORGUNIT;
			}
			if( Ext.getCmp('userOrgunitChildren').getValue() === true ){
				params += '&dimension=ou:' + TR.conf.userOrgunit.USER_ORGUNIT_CHILDREN;
			}
			
			// Selected orgunits
			
			if( Ext.getCmp('userOrgunit').getValue() === false 
				&& Ext.getCmp('userOrgunitChildren').getValue() === false){
				var ou = "ou:";
				for( var i in TR.state.orgunitIds){
					ou += TR.state.orgunitIds[i];
					if( i<TR.state.orgunitIds.length - 1 ){
						ou +=";"
					}
				}
				params += '&dimension=' + ou;
			}
			
			// Filter
			
			var idx = 0;
			TR.cmp.params.dataelement.selected.store.each( function(r) {
				var valueType = r.data.valueType;
				var deId = r.data.id;
				var length = Ext.getCmp('filterPanel_' + deId).items.length/45;
				var hidden = TR.state.caseBasedReport.isColHidden(deId);
				var dimensionOption = 'dimension';

				for(var idx=0;idx<length;idx++)
				{
					var id = deId + '_' + idx;
					if( idx==0 )
					{
						dimensionOption = Ext.getCmp('filter_dimension_' + deId ).getValue();
					}
					
					var filterOpt = Ext.getCmp('filter_opt_' + id).getValue();						
					var filterValue = Ext.getCmp('filter_' + id).rawValue;
					if(deId=='GENDER' || Ext.getCmp('filter_' + id).xtype=='combobox'){	
						filterValue = Ext.getCmp('filter_' + id).getValue();
					}
					
					var filter = deId;
					if( Ext.getCmp('filter_' + id).getValue()!=null 
						&& Ext.getCmp('filter_' + id).getValue()!=''){
						
						filterValue = filterValue.toLowerCase();
						filter += ':' + filterOpt + ':';
						if( filterOpt == 'IN' )
						{
							filter +=filterValue.replace(/:/g,";"); 
						}
						else
						{
							filter += Ext.getCmp('filter_' + id).getValue();
						}
					}
					
					if( dimensionOption=='dimension' ){
						params += '&dimension=' + filter;
					}
					else{
						params += '&filter=' + filter;
					}
				}
			});
			
			var reportType = Ext.getCmp('reportTypeGroup').getValue().reportType;
			if(reportType=='true')
			{
				// Order-by
				
				if(TR.state.asc!=""){				
					params += '&asc=' + TR.state.asc;
				}
				else if(TR.state.desc!=""){
					params += '&desc=' + TR.state.desc;
				}
			}
			else{
				if(Ext.getCmp('limitOption').getValue()!==null){
					params += '&limit=' + Ext.getCmp('limitOption').getValue();
				}
				
				params += '&position=' + TR.state.aggregateReport.getPosition();
				
				// Relative periods
				var rperiod = TR.cmp.params.relativeperiod.checkbox;
				var period = '';
				Ext.Array.each(rperiod, function(item) {
					if(item.getValue() && !item.hidden){
						period += item.paramName + ";";
					}
				});
				if( period.length > 0 ){
					period = period.substr(0,period.length-1);
					params += '&dimension=pe:' + period;
				}
				
				// Sort-order
				params += '&sortOrder=' + TR.state.sortOrder;
			}
			
			return params;
		},
		
		caseBasedReport: {
			generate: function( type ) {
			
				// Validation
				
				if( !TR.state.caseBasedReport.validation.objects() ){
					return;
				}
				
				// Get url
				var programId = Ext.getCmp('programCombobox').getValue(); 
				var programStageId = TR.cmp.params.programStage.getValue();
				var url = TR.conf.finals.ajax.path_api + TR.conf.finals.ajax.generatetabularreport_get;
				
				// Export to XLS 
				
				if( type)
				{
					url +=  programId + "." + type 
					if (Ext.getCmp('programStageCombobox').getValue() != '') {
						url += "?stage=" + programStageId;
					}
					else{
						url += "?";
					}
					url+= TR.state.getURLParams();
					document.location = url;
				}
				// Show report on grid
				else
				{
					url += programId + ".json?";
					if (Ext.getCmp('programStageCombobox').getValue() != '') {
						url += "stage=" + programStageId + "&";
					}
					// Paging
					if(TR.state.currentPage==undefined){
						TR.state.currentPage = 1;
					}
					url += "page=" + TR.state.currentPage;
					
					TR.util.mask.showMask(TR.cmp.region.center, TR.i18n.loading);
					Ext.Ajax.request({
						url: url,
						method: "GET",
						disableCaching: false,
						scope: this,
						params: Ext.urlEncode(TR.state.getParams()).replace(/%3B/g,";").replace(/%3A/g,":"),
						success: function(r) {
							var json = Ext.JSON.decode(r.responseText);
							
							TR.value.columns = json.headers;
							TR.value.values = json.rows;
							TR.state.total = json.metaData.pager.total;
							TR.state.pageCount = json.metaData.pager.pageCount;
							
							// Get fields
							
							var fields = [];
							for( var index=0; index < TR.value.columns.length; index++ )
							{
								fields[index] = TR.value.columns[index].name;
							}
							TR.value.fields = fields;
							
							// Set data for grid
							
							TR.store.getDataTableStore();
							TR.datatable.getDataTable();
							
							TR.datatable.setPagingToolbarStatus();
							TR.util.mask.hideMask();
						}
					});
				}
				TR.util.notification.ok();
			},
			isColHidden: function( colname ) {
				var grid = TR.datatable.datatable;
				if( grid != null ){
					var cols = grid.columns;
					for (var i = 0; i < cols.length; i++) {
						if (cols[i].name == colname) {
							return(cols[i].hidden==undefined)? false : cols[i].hidden;
						}
					}
				} 
				return false;
			},
			validation: {
				objects: function() {
					
					if (TR.cmp.settings.program.getValue() == null) {
						TR.util.notification.error(TR.i18n.et_no_programs, TR.i18n.et_no_programs);
						return false;
					}
					
					if( !TR.cmp.settings.startDate.isValid() )
					{
						var message = TR.i18n.start_date + " " + TR.i18n.is_not_valid;
						TR.util.notification.error( message, message);
						return false;
					}
					
					if( !TR.cmp.settings.endDate.isValid() )
					{
						var message = TR.i18n.end_date + " " + TR.i18n.is_not_valid;
						TR.util.notification.error( message, message);
						return false;
					}
				
					if (TR.state.orgunitIds.length == 0 
						&& TR.cmp.aggregateFavorite.userorganisationunit.getValue() == 'false'
						&& TR.cmp.aggregateFavorite.userorganisationunitchildren.getValue() == 'false' ) {
						TR.util.notification.error(TR.i18n.et_no_orgunits, TR.i18n.em_no_orgunits);
						return false;
					}
					
					if(TR.cmp.params.dataelement.selected.store.data.items.length > 0 )
					{
						var isvalid = true;
						TR.cmp.params.dataelement.selected.store.each( function(r) {
							var valueType = r.data.valueType;
							var deId = r.data.id;
							var length = Ext.getCmp('filterPanel_' + deId).items.length/5;
							var hidden = TR.state.caseBasedReport.isColHidden(deId);
							var dimensionOption = 'dimension';
							
							for(var idx=0;idx<length;idx++)
							{
								var id = deId + '_' + idx;
								if(Ext.getCmp('filter_' + id)!=undefined)
								{
									if( idx==0 )
									{
										dimensionOption = Ext.getCmp('filter_dimension_' + deId ).getValue();
										if( dimensionOption=='filter' ){
											var filterValue = Ext.getCmp('filter_' + id).rawValue.toLowerCase();
											if(filterValue==null ||filterValue=='' ){
												isvalid = false;
											}
										}
									}
									
								}
							}
						});
						
						if(	!isvalid ){
							TR.util.notification.error(TR.i18n.fill_filter_values_for_all_selected_data_elements, TR.i18n.fill_filter_values_for_all_selected_data_elements);
							return false;
						}
					}
				
					return true;
				},
				response: function(r) {
					if (!r.responseText) {
						TR.util.mask.hideMask();
						TR.util.notification.error(TR.i18n.et_invalid_uid, TR.i18n.em_invalid_uid);
						return false;
					}
					return true;
				},
				value: function() {
					if (!TR.value.values.length) {
						TR.util.mask.hideMask();
						TR.util.notification.error(TR.i18n.et_no_data, TR.i18n.em_no_data);
						return false;
					}
					return true;
				}
			}
		},
		
		aggregateReport: {
			generate: function( type ) {
				
				// Validation
				if( !TR.state.aggregateReport.validation.objects() ){
					return;
				}
				
				// Get url
				var programId = Ext.getCmp('programCombobox').getValue(); 
				var programStageId = TR.cmp.params.programStage.getValue();
				var url = TR.conf.finals.ajax.path_api + TR.conf.finals.ajax.generateaggregatereport_get;
				
				// Export to XLS
				if( type)
				{
					url += programId + "." + type 
					if (Ext.getCmp('programStageCombobox').getValue() != '') {
						url += "?stage=" + programStageId;
					}
					else{
						url += "?";
					}
					url +=  TR.state.getURLParams();
					document.location = url;
				}
				// Show report on grid
				else
				{
					url += programId + ".json?";
					if (Ext.getCmp('programStageCombobox').getValue() != '') {
						url += "stage=" + programStageId;
					}
			
					TR.util.mask.showMask(TR.cmp.region.center, TR.i18n.loading);
					Ext.Ajax.request({
						url: url,
						disableCaching: false,
						method: "GET",
						scope: this,
						params: Ext.urlEncode(TR.state.getParams()).replace(/%3B/g,";").replace(/%3A/g,":"),
						success: function(r) {
							var json = Ext.JSON.decode(r.responseText);
							
							TR.value.columns = json.headers;
							var rows = json.rows;
							for( var i in rows ){
								var cols = rows[i];
								for( var j in cols ){
									var value = json.metaData.names[cols[j]];
									if(value!=undefined){
										rows[i][j] = value;
									}
								}
							}
							
							TR.value.values = rows;
							
							// Get fields
							
							var fields = [];
							var index=0;
							for( index=0; index < TR.value.columns.length - 1; index++ )
							{
								fields[index] = {
									name:TR.value.columns[index].name,
									type: 'string'
								};
							}
							fields[index] = {
								name:TR.value.columns[index].name,
								type: 'number'
							};
							
							TR.value.fields = fields;
							
							// Set data for grid
							
							TR.store.getDataTableStore();
							TR.datatable.getDataTable();
							TR.datatable.hidePagingBar();
							TR.util.mask.hideMask();
						}
					});
				}
				TR.util.notification.ok();
			},
			getPosition: function() {
				// 1 - Rows
				// 2 - Columns
				// 3 - Filter
				
				var positionOrgunit = TR.state.aggregateReport.getOrgunitPosition();
				var positionPeriod = TR.state.aggregateReport.getPeriodPosition();
				var positionData = TR.state.aggregateReport.getDataPosition();
				
				// 1
				if( positionOrgunit==1 && positionPeriod==2 && positionData==3 )
				{
					return TR.conf.reportPosition.POSITION_ROW_ORGUNIT_COLUMN_PERIOD;
				}
				// 2
				if( positionOrgunit==2 && positionPeriod==1 && positionData==3 )
				{
					return TR.conf.reportPosition.POSITION_ROW_PERIOD_COLUMN_ORGUNIT;
				}
				// 3
				if( positionOrgunit==1 && positionPeriod==1 && positionData==3 )
				{
					return TR.conf.reportPosition.POSITION_ROW_ORGUNIT_ROW_PERIOD;
				}
				// 4
				if( positionOrgunit==3 && positionPeriod==1 && positionData==3 )
				{
					return TR.conf.reportPosition.POSITION_ROW_PERIOD;
				}
				// 5
				if( positionOrgunit==1 && positionPeriod==3 && positionData==3 )
				{
					return TR.conf.reportPosition.POSITION_ROW_ORGUNIT;
				}
				//6
				if( positionOrgunit==3 && positionPeriod==1 && positionData==2 )
				{
					return TR.conf.reportPosition.POSITION_ROW_PERIOD_COLUMN_DATA;
				}
				//7
				if( positionOrgunit==1 && positionPeriod==3 && positionData==2 )
				{
					return TR.conf.reportPosition.POSITION_ROW_ORGUNIT_COLUMN_DATA;
				}
				// 8
				if( positionOrgunit==3 && positionPeriod==3 && positionData==1 )
				{
					return TR.conf.reportPosition.POSITION_ROW_DATA;
				}
				// 9
				if( positionOrgunit==3 && positionPeriod==2 && positionData==1 )
				{
					return TR.conf.reportPosition.POSITION_ROW_DATA_COLUMN_PERIOD;
				}
				// 10
				if( positionOrgunit==2 && positionPeriod==3 && positionData==1 )
				{
					return TR.conf.reportPosition.POSITION_ROW_DATA_COLUMN_ORGUNIT;
				}
				return '';
			},
			getOrgunitPosition: function() {
				
				 // Filter position
				 var orgunitPosition = 3;
				
				// Row position
				Ext.getCmp('positionRowCbx').store.each( function(r) {
					if(r.data.id==1) // is orgunit
					{
						orgunitPosition = 1; // row
					}
				});
				
				// Column position
				Ext.getCmp('positionColCbx').store.each( function(r) {
					if(r.data.id==1) // is orgunit
					{
						orgunitPosition = 2; // col
					}
				});
				
				return orgunitPosition; // filter
			},
			getPeriodPosition: function() {
				var periodPosition = 3;
				// period position
				Ext.getCmp('positionRowCbx').store.each( function(r) {
					if(r.data.id==2) // is period
					{
						periodPosition = 1;
					}
				});
				
				// Column position
				Ext.getCmp('positionColCbx').store.each( function(r) {
					if(r.data.id==2) // is period
					{
						periodPosition = 2;
					}
				});
				
				return periodPosition; // filter
			},
			getDataPosition: function() {
				
				var positionFilter = 3;
				// period position
				Ext.getCmp('positionRowCbx').store.each( function(r) {
					if(r.data.id==3) // is data
					{
						positionFilter = 1;
					}
				});
				
				// Column position
				Ext.getCmp('positionColCbx').store.each( function(r) {
					if(r.data.id==3) // is data
					{
						positionFilter = 2;
					}
				});
				
				return positionFilter; // filter
			},
			validation: {
				objects: function() {
					if (TR.cmp.settings.program.getValue() == null) {
						TR.util.notification.error(TR.i18n.et_no_programs, TR.i18n.et_no_programs);
						return false;
					}
					
					// Validate date
					
					if( TR.cmp.settings.startDate.rawValue != "" 
						&& !TR.cmp.settings.startDate.isValid() )
					{
						var message = TR.i18n.start_date + " " + TR.i18n.is_not_valid;
						TR.util.notification.error( message, message);
						return false;
					}
					
					if( TR.cmp.settings.endDate.rawValue != "" 
						&& !TR.cmp.settings.endDate.isValid() )
					{
						var message = TR.i18n.end_date + " " + TR.i18n.is_not_valid;
						TR.util.notification.error( message, message);
						return false;
					}
					
					if( TR.cmp.settings.startDate.rawValue==""
						&& TR.cmp.settings.startDate.rawValue == "" )
					{
						var flag = false;
						var relativePeriodList = TR.cmp.params.relativeperiod.checkbox;
						Ext.Array.each(relativePeriodList, function(item) {
							if(item.getValue()){
								flag = true;
							}
						});
						
						if( !flag )
						{
							TR.util.notification.error(TR.i18n.em_no_period, TR.i18n.em_no_period);
							return false;
						}
					}
					
					// Validate orgunit
					
					if (TR.state.orgunitIds.length == 0 
						&& TR.cmp.aggregateFavorite.userorganisationunit.getValue() == 'false'
						&& TR.cmp.aggregateFavorite.userorganisationunitchildren.getValue() == 'false' ) {
						TR.util.notification.error(TR.i18n.et_no_orgunits, TR.i18n.em_no_orgunits);
						return false;
					}
					
					// Validate data element
					
					if(TR.cmp.params.dataelement.selected.store.data.items.length > 0 )
					{
						var isvalid = true;
						TR.cmp.params.dataelement.selected.store.each( function(r) {
							var valueType = r.data.valueType;
							var deId = r.data.id;
							var length = Ext.getCmp('filterPanel_' + deId).items.length/5;
							var hidden = TR.state.caseBasedReport.isColHidden(deId);
							var dimensionOption = 'dimension';
							
							for(var idx=0;idx<length;idx++)
							{
								var id = deId + '_' + idx;
								if(Ext.getCmp('filter_' + id)!=undefined)
								{
									if( idx==0 )
									{
										dimensionOption = Ext.getCmp('filter_dimension_' + deId ).getValue();
										if( dimensionOption=='filter' ){
											var filterValue = Ext.getCmp('filter_' + id).rawValue.toLowerCase();
											if(filterValue==null ||filterValue=='' ){
												isvalid = false;
											}
										}
									}
									
								}
							}
						});
						
						if(	!isvalid ){
							TR.util.notification.error(TR.i18n.fill_filter_values_for_all_selected_data_elements, TR.i18n.fill_filter_values_for_all_selected_data_elements);
							return false;
						}
					}
					
					
					var periodInt = 0;
					if( TR.cmp.settings.startDate.rawValue!="" 
						&& TR.cmp.settings.endDate.rawValue!="") 
					{
						periodInt++;
					}
					
					var relativePeriodList = TR.cmp.params.relativeperiod.checkbox;
					Ext.Array.each(relativePeriodList, function(item) {
						if(item.getValue()){
							periodInt++;
						}
					});
					
					var position = TR.state.aggregateReport.getPosition();
					var o = TR.state.aggregateReport.getOrgunitPosition();
					var p = TR.state.aggregateReport.getPeriodPosition();
					var d = TR.state.aggregateReport.getDataPosition();
					if( position==''){
						if( o!='1' && p!='1' && d!='1')
						{
							TR.util.notification.error(TR.i18n.please_select_one_position_for_row, TR.i18n.please_select_one_position_for_row);
							return false;
						}
						if( o!='3' && p!='3' && d!='3')
						{
							TR.util.notification.error(TR.i18n.please_select_one_position_for_filter, TR.i18n.please_select_one_position_for_filter);
							return false;
						}
						else {
							TR.util.notification.error(TR.i18n.invalid_position, TR.i18n.invalid_position);
							return false;
						}
					}
					
					if( Ext.getCmp('aggregateType').getValue().aggregateType != 'count'
						&& ( Ext.getCmp('deSumCbx').getValue() == null || Ext.getCmp('deSumCbx').getValue=='')){
						TR.util.notification.error(TR.i18n.select_a_dataelement_for_sum_avg_operator, TR.i18n.select_a_dataelement_for_sum_avg_operator );
						return false;
					}
				
					// Check orgunit by period
					if( o == 3 && ( TR.state.orgunitIds.length > 1 
						|| Ext.getCmp('userOrgunitChildren').getValue() ))
					{
						TR.util.notification.error(TR.i18n.multiple_orgunits_selected_as_filter, TR.i18n.multiple_orgunits_selected_as_filter);
					}
				
					// Check filter by period
					if( p == 3 )
					{
						var noPeriod = 0;
						if( TR.cmp.settings.startDate.rawValue!="" && TR.cmp.settings.startDate.rawValue!="" ){
							noPeriod++;
						}
						
						var relativePeriodList = TR.cmp.params.relativeperiod.checkbox;
						Ext.Array.each(relativePeriodList, function(item) {
							if(item.getValue() && !item.hidden 
								&&( item.paramName=='last3Months' 
								  || item.paramName=='last12Months' 
								  || item.paramName=='last4Quarters' 
								  || item.paramName=='last2SixMonths' 
								  || item.paramName=='last5Years' ) ){
								noPeriod += 2;
							}
						});
						
						if( noPeriod > 1 ){
							TR.util.notification.error(TR.i18n.multiple_periods_selected_as_filter, TR.i18n.multiple_periods_selected_as_filter);
						}
					}
				
					return true;
				},
				response: function(r) {
					if (!r.responseText) {
						TR.util.mask.hideMask();
						TR.util.notification.error(TR.i18n.et_invalid_uid, TR.i18n.em_invalid_uid);
						return false;
					}
					return true;
				},
				value: function() {
					if (!TR.value.values.length) {
						TR.util.mask.hideMask();
						TR.util.notification.error(TR.i18n.et_no_data, TR.i18n.em_no_data);
						return false;
					}
					return true;
				}
			}
		}
   };
   
    TR.value = {
		title: '',
		columns: [],
		fields: [],
		values: [],
		covertValueType: function( type )
		{
			type = type.toLowerCase();
			if( type == 'date' )
			{
				return type;
			}
			if(type == 'number')
			{
				return 'float';
			}
			if( type == 'int' || type == 'positiveNumber'  || type == 'negativeNumber' || type == 'zeroPositiveInt' )
			{
				return 'numeric';
			}
			if( type == 'bool' || type == 'yes/no' || type == 'trueOnly' )
			{
				return 'boolean';
			}
			if( type == 'combo' || type == 'username' )
			{
				return 'list';
			}
			return 'string';
		},
		covertXType: function( type )
		{
			if( type == 'date' )
			{
				return 'datefield';
			}
			if( type == 'number' || type == 'int' || type == 'positiveNumber'  || type == 'negativeNumber' || type == 'zeroPositiveInt' )
			{
				return 'numberfield';
			}
			if( type == 'combo' || type == 'list' || type == 'username' || type == 'trueOnly' || type=='bool' )
			{
				return 'combobox';
			}
			return 'textfield';
		},
	};
      
    TR.datatable = {
        datatable: null,
		getDataTable: function() {
			var cols = [];
			var reportType = Ext.getCmp('reportTypeGroup').getValue().reportType;
			if( reportType=='true' ){
				cols = this.createCaseColTable();
			}
			else{
				cols = this.createAggColTable();
			}

			// title
			var title = TR.cmp.settings.program.rawValue + " - " + TR.cmp.params.programStage.rawValue + " " + TR.i18n.report;
			if(reportType=='false'){
				title = TR.value.title;
			}
			
			// grid
			this.datatable = Ext.create('Ext.grid.Panel', {
                height: TR.util.viewport.getSize().y - 58,
				id: 'gridTable',
				columns: {
					items: cols,
					listeners: {
						headerclick: function(container, column, e) {
							if( column.sortable ){
								if( reportType == 'true' ){
									if(column.sortState=='ASC'){
										TR.state.asc = column.dataIndex;
										TR.state.desc = "";
									}
									else{
										TR.state.asc = "";
										TR.state.desc = column.dataIndex;
									}
									TR.exe.execute(false, true );
								}
								else{
									if( TR.state.sortOrder=='ASC'){
										TR.state.sortOrder = "DESC";
									}
									else{
										TR.state.sortOrder = "ASC";
									}
								}
							}
						}
					}
				},
				scroll: 'both',
				title: title,
				selType: 'cellmodel',
				bbar: [
					{
						xtype: 'button',
						icon: 'images/arrowleftdouble.png',
						id:'firstPageBtn',
						width: 22,
						handler: function() {
							TR.exe.paging(1);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowleft.png',
						id:'previousPageBtn',
						width: 22,
						handler: function() {
							TR.exe.paging( eval(TR.cmp.settings.currentPage.rawValue) - 1 );
						}
					},
					{
						xtype: 'label',
						id:'separate1Lbl',
						text: '|'
					},
					{
						xtype: 'label',
						id:'pageLbl',
						text: TR.i18n.page
					},
					{
						xtype: 'textfield',
						cls: 'tr-textfield-alt1',
						id:'currentPage',
						value: TR.state.currentPage,
						listeners: {
							added: function() {
								TR.cmp.settings.currentPage = this;
							},
							specialkey: function(f,e){
								 if(e.getKey() == e.ENTER){
									TR.exe.paging( Ext.getCmp('currentPage').getValue() );
								}
							}
						},
					},
					{
						xtype: 'label',
						id:'totalPageLbl',
						text: ' of ' + TR.state.pageCount + ' | '
					},
					{
						xtype: 'button',
						icon: 'images/arrowright.png',
						id:'nextPageBtn',
						handler: function() {
							TR.exe.paging( eval(TR.cmp.settings.currentPage.rawValue) + 1 );
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowrightdouble.png',
						id:'lastPageBtn',
						handler: function() {
							TR.exe.paging( TR.state.pageCount );
						}
					},
					{
						xtype: 'label',
						id:'separate2Lbl',
						text: '|'
					},
					{
						xtype: 'button',
						id:'refreshBtn',
						icon: 'images/refresh.png',
						handler: function() {
							TR.exe.paging( TR.cmp.settings.currentPage.rawValue );
						}
					},
					'->',
					{
						xtype: 'label',
						id: 'totalEventLbl',
						style: 'margin-right:18px;',
						text: TR.state.total + ' ' + TR.i18n.events
					},
				], 
				store: TR.store.datatable
			});
			
			Ext.override(Ext.grid.header.Container, { 
				sortAscText: TR.i18n.asc,
				sortDescText: TR.i18n.desc, 
				columnsText: TR.i18n.show_hide_columns });

			TR.cmp.region.center.removeAll(true);
			TR.cmp.region.center.add(this.datatable);		
          	
            return this.datatable;
            
        },
		createCaseColTable: function(){
			var cols = [];
			
			for( var i=0; i <TR.value.columns.length; i++ )
			{
				// hidden cols
				if( i<2 || i==6 || i==7 ){
					cols[i] = {
						header: TR.value.columns[i].column, 
						dataIndex: TR.value.columns[i].name,
						height: TR.conf.layout.east_gridcolumn_height,
						name: TR.value.columns[i].column,
						sortable: true,
						draggable: false,
						hideable: true,
						hidden: true,
						menuDisabled: true
					}
				}
				// Sortable columns
				else if( i==2 || i== 5 ){
					cols[i] = {
						header: TR.value.columns[i].column, 
						dataIndex: TR.value.columns[i].name,
						height: TR.conf.layout.east_gridcolumn_height,
						name: TR.value.columns[i].column,
						sortable: true,
						draggable: false,
						hideable: false,
						menuDisabled: true
					}
				}
				else{
					cols[i] = {
						header: TR.value.columns[i].column, 
						dataIndex: TR.value.columns[i].name,
						height: TR.conf.layout.east_gridcolumn_height,
						name: TR.value.columns[i].column,
						sortable: false,
						draggable: false,
						hideable: false,
						menuDisabled: false
					}
				}
			}
				
			return cols;
		},
		createAggColTable: function(){
			var cols = [];
			var i = 0;
			for( i=0; i <TR.value.columns.length - 1; i++ )
			{
				cols[i] = {
					header: TR.value.columns[i].column, 
					dataIndex: TR.value.columns[i].name,
					height: TR.conf.layout.east_gridcolumn_height,
					name: TR.value.columns[i].column,
					sortable: false,
					draggable: false,
					hideable: false,
					menuDisabled: true
				}
			}
			
			cols[i] = {
				header: TR.value.columns[i].column, 
				dataIndex: TR.value.columns[i].name,
				height: TR.conf.layout.east_gridcolumn_height,
				name: TR.value.columns[i].column, 
				type: 'number',
				sortable: true,
				draggable: false,
				hideable: false,
				menuDisabled: true
			}
			
			return cols;
		},
		createColumn: function( type, id, colname, index ){
			var objectType = id.split('_')[0];
			var objectId = id.split('_')[1];
			
			var params = {};
			params.header = colname;
			params.dataIndex = index;
			params.name = id;
			params.hidden = eval(TR.value.columns[index].hidden );
			params.menuFilterText = TR.value.filter;
			params.sortable = false;
			params.draggable = true;
			
			if(Ext.getCmp('reportTypeGroup').getValue().reportType=='false')
			{
				params.menuDisabled = true;
				params.draggable = false;
			}
			params.isEditAllowed = true;
			type = type.toLowerCase();
			if( type == 'date' )
			{
				params.renderer = Ext.util.Format.dateRenderer( TR.i18n.format_date );
			}
			return params;
		},
        setPagingToolbarStatus: function() {
			TR.datatable.showPagingBar();
			Ext.getCmp('currentPage').enable();
			Ext.getCmp('totalEventLbl').setText( TR.state.total + ' ' + TR.i18n.events );
			Ext.getCmp('totalPageLbl').setText( ' of ' + TR.state.pageCount + ' | ' );
			if( TR.state.total== 0 )
			{
				Ext.getCmp('currentPage').setValue('');
				Ext.getCmp('currentPage').setValue('');
				Ext.getCmp('currentPage').disable();
				Ext.getCmp('firstPageBtn').disable();
				Ext.getCmp('previousPageBtn').disable();
				Ext.getCmp('nextPageBtn').disable();
				Ext.getCmp('lastPageBtn').disable();
	
				Ext.getCmp('btnClean').disable();
			}
			else
			{
				Ext.getCmp('btnClean').enable();
				Ext.getCmp('currentPage').setValue(TR.state.currentPage);
				
				if( TR.state.currentPage == TR.state.pageCount 
					&& TR.state.pageCount== 1 )
				{
					Ext.getCmp('firstPageBtn').disable();
					Ext.getCmp('previousPageBtn').disable();
					Ext.getCmp('nextPageBtn').disable();
					Ext.getCmp('lastPageBtn').disable();
				}
				else if( TR.state.currentPage == TR.state.pageCount )
				{
					Ext.getCmp('firstPageBtn').enable();
					Ext.getCmp('previousPageBtn').enable();
					Ext.getCmp('nextPageBtn').disable();
					Ext.getCmp('lastPageBtn').disable();
				}
				else if( TR.state.currentPage == 1 )
				{
					Ext.getCmp('firstPageBtn').disable();
					Ext.getCmp('previousPageBtn').disable();
					Ext.getCmp('nextPageBtn').enable();
					Ext.getCmp('lastPageBtn').enable();
				}
				else
				{
					Ext.getCmp('firstPageBtn').enable();
					Ext.getCmp('previousPageBtn').enable();
					Ext.getCmp('nextPageBtn').enable();
					Ext.getCmp('lastPageBtn').enable();
				} 
			}
        },
		hidePagingBar: function(){
			Ext.getCmp('currentPage').setVisible(false);
			Ext.getCmp('totalEventLbl').setVisible(false);
			Ext.getCmp('totalPageLbl').setVisible(false);
			Ext.getCmp('currentPage').setVisible(false);
			Ext.getCmp('currentPage').setVisible(false);
			Ext.getCmp('currentPage').setVisible(false);
			Ext.getCmp('firstPageBtn').setVisible(false);
			Ext.getCmp('previousPageBtn').setVisible(false);
			Ext.getCmp('nextPageBtn').setVisible(false);
			Ext.getCmp('lastPageBtn').setVisible(false);
			Ext.getCmp('refreshBtn').setVisible(false);
			Ext.getCmp('pageLbl').setVisible(false);
			Ext.getCmp('separate1Lbl').setVisible(false);
			Ext.getCmp('separate2Lbl').setVisible(false);
		},
		showPagingBar: function(){
			Ext.getCmp('currentPage').setVisible(true);
			Ext.getCmp('totalEventLbl').setVisible(true);
			Ext.getCmp('totalPageLbl').setVisible(true);
			Ext.getCmp('currentPage').setVisible(true);
			Ext.getCmp('currentPage').setVisible(true);
			Ext.getCmp('currentPage').setVisible(true);
			Ext.getCmp('firstPageBtn').setVisible(true);
			Ext.getCmp('previousPageBtn').setVisible(true);
			Ext.getCmp('nextPageBtn').setVisible(true);
			Ext.getCmp('lastPageBtn').setVisible(true);
			Ext.getCmp('refreshBtn').setVisible(true);
			Ext.getCmp('pageLbl').setVisible(true);
			Ext.getCmp('separate1Lbl').setVisible(true);
			Ext.getCmp('separate2Lbl').setVisible(true);
		}
    };
        
	TR.exe = {
		execute: function( type) {
			TR.state.generateReport( type );
		},
		paging: function( currentPage )
		{
			TR.state.currentPage = currentPage;
			Ext.getCmp('currentPage').setValue( currentPage );	
			TR.datatable.setPagingToolbarStatus();
		},
		datatable: function() {
			TR.store.getDataTableStore();
			TR.datatable.getDataTable();
			TR.datatable.setPagingToolbarStatus();
		}
    };
	
	TR.app = {};
	
	TR.app.CaseFavoriteWindow = function() {

		// Objects
		var NameWindow,

		// Instances
			nameWindow,
	
		// Components
			addButton,
			searchTextfield,
			grid,
			prevButton,
			nextButton,
			tbar,
			bbar,
			info,
			nameTextfield,
			createButton,
			updateButton,
			cancelButton,
			favoriteWindow,

		// Vars
			windowWidth = 500,
			windowCmpWidth = windowWidth - 22;

		TR.store.caseBasedFavorite.on('load', function(store, records) {
			var pager = store.proxy.reader.jsonData.pager;

			info.setText( TR.i18n.page + ' ' + pager.currentPage + TR.i18n.of + ' ' + pager.pageCount );

			prevButton.enable();
			nextButton.enable();

			if (pager.currentPage === 1) {
				prevButton.disable();
			}

			if (pager.currentPage === pager.pageCount) {
				nextButton.disable();
			}
		});
		
		NameWindow = function(id) {
			var window;
			var record = TR.store.caseBasedFavorite.getById(id);
			
			nameTextfield = Ext.create('Ext.form.field.Text', {
				height: 26,
				width: 371,
				fieldStyle: 'padding-left: 6px; border-radius: 1px; border-color: #bbb; font-size:11px',
				style: 'margin-bottom:0',
				emptyText: TR.i18n.favorite_name,
				value: id ? record.data.name : '',
				listeners: {
					afterrender: function() {
						this.focus();
					}
				}
			});
			
			createButton = Ext.create('Ext.button.Button', {
				text: TR.i18n.create,
				handler: function() {
					var name = nameTextfield.getValue();

					if (name) {
						
						// Validation

						if( !TR.state.caseBasedReport.validation.objects() )
						{
							return;
						}
						
						// Save favorite
						
						TR.util.mask.showMask(TR.cmp.caseBasedFavorite.window, TR.i18n.saving + '...');
						var p = TR.state.getParams(false);
						p.name = name;
						p.programId = Ext.getCmp('programCombobox').getValue(); 
						p.programStageId = TR.cmp.params.programStage.getValue();
						
						Ext.Ajax.request({
							url: TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.casebasedfavorite_validate,
							disableCaching: false,
							method: 'POST',
							params: {name:name},
							success: function(r) {
									var json = Ext.JSON.decode(r.responseText);
									if(json.response=='success'){
										Ext.Ajax.request({
											url: TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.casebasedfavorite_save,
											disableCaching: false,
											method: 'POST',
											params: p,
											success: function() {
												TR.store.caseBasedFavorite.loadStore();
												window.destroy();
												TR.util.mask.hideMask();
											}
										})
									}
									else{
										TR.util.notification.error(TR.i18n.error, json.message);
										window.destroy();
										TR.util.mask.hideMask();
									}
								}
							});
					}
				}
			});
			
			updateButton = Ext.create('Ext.button.Button', {
				text: TR.i18n.update,
				handler: function() {
					var name = nameTextfield.getValue();

					if (id && name) {
						TR.util.mask.showMask(TR.cmp.caseBasedFavorite.window, TR.i18n.renaming + '...');
						
						Ext.Ajax.request({
							url: TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.casebasedfavorite_validate,
							disableCaching: false,
							method: 'POST',
							params: {id:id, name:name},
							success: function(r) {
									var json = Ext.JSON.decode(r.responseText);
									if(json.response=='success'){
										Ext.Ajax.request({
											url:  TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.casebasedfavorite_rename,
											disableCaching: false,
											method: 'POST',
											params: {id: id, name: name},
											failure: function(r) {
												TR.util.mask.hideMask();
												alert(r.responseText);
											},
											success: function() {
												TR.store.caseBasedFavorite.loadStore();
												window.destroy();
												TR.util.mask.hideMask();
											}
										});
									}
									else{
										TR.util.notification.error(TR.i18n.error, json.message);
										window.destroy();
										TR.util.mask.hideMask();
									}
								}
							});
						}
					}
			});
			
			cancelButton = Ext.create('Ext.button.Button', {
				text: TR.i18n.cancel,
				handler: function() {
					window.destroy();
				}
			});
			
			window = Ext.create('Ext.window.Window', {
				title: id ? TR.i18n.rename_favorite : TR.i18n.create_new_favorite,
				bodyStyle: 'padding:2px; background:#fff',
				resizable: false,
				modal: true,
				items: nameTextfield,
				bbar: [
					cancelButton,
					'->',
					id ? updateButton : createButton
				],
				listeners: {
					show: function(w) {
						TR.util.window.setAnchorPosition(w, addButton);
					}
				}
			});

			return window;
		};

		addButton = Ext.create('Ext.button.Button', {
			text: TR.i18n.add_new,
			width: 75,
			height: 26,
			style: 'border-radius: 1px;',
			menu: {},
			handler: function() {
				nameWindow = new NameWindow(null, TR.i18n.create);
				nameWindow.show();
			}
		});

		searchTextfield = Ext.create('Ext.form.field.Text', {
			width: windowCmpWidth - addButton.width - 11,
			height: 26,
			fieldStyle: 'padding-right: 0; padding-left: 6px; border-radius: 1px; border-color: #bbb; font-size:11px',
			emptyText: TR.i18n.search_for_favorites,
			enableKeyEvents: true,
			currentValue: '',
			listeners: {
				keyup: function() {
					if (this.getValue() !== this.currentValue) {
						this.currentValue = this.getValue();

						var value = this.getValue();
						var url = ( value ) ? TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.casebasedfavorite_getall + "?query=" + value : null;
						var store = TR.store.caseBasedFavorite;

						store.currentPage = 1;
						store.loadStore(url);
					}
				}
			}
		});

		prevButton = Ext.create('Ext.button.Button', {
			text: TR.i18n.prev,
			handler: function() {
				var value = searchTextfield.getValue();
				var url = ( value ) ? TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.casebasedfavorite_getall + "?query=" + value : null;
				var store = TR.store.caseBasedFavorite;

				store.currentPage = store.currentPage <= 1 ? 1 : store.currentPage - 1;
				store.loadStore(url);
			}
		});

		nextButton = Ext.create('Ext.button.Button', {
			text: TR.i18n.next,
			handler: function() {
				var value = searchTextfield.getValue();
				var url = ( value ) ? TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.casebasedfavorite_getall + "?query=" + value : null;
				var store = TR.store.caseBasedFavorite;

				store.currentPage = store.currentPage + 1;
				store.loadStore(url);
			}
		});

		info = Ext.create('Ext.form.Label', {
			cls: 'tr-label-info',
			width: 300,
			height: 22
		});
		
		grid = Ext.create('Ext.grid.Panel', {
			cls: 'tr-grid',
			scroll: false,
			hideHeaders: true,
			columns: [
				{
					dataIndex: 'name',
					sortable: false,
					width: windowCmpWidth - 88,
					renderer: function(value, metaData, record) {
						var fn = function() {
							var element = Ext.get(record.data.id);
							if (element) {
								element = element.parent('td');
								element.addClsOnOver('link');
								element.load = function() {
									favoriteWindow.hide();
									TR.util.crud.favorite.run( record.data.id );
								};
								element.dom.setAttribute('onclick', 'Ext.get(this).load();');
							}
						};

						Ext.defer(fn, 100);

						return '<div id="' + record.data.id + '">' + value + '</div>';
					}
				},
				{
					xtype: 'actioncolumn',
					sortable: false,
					width: 80,
					items: [
						{
							iconCls: 'tr-grid-row-icon-edit',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-edit' + (!record.data.access.update ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex);
								nameWindow = new NameWindow(record.data.id);
								nameWindow.show();
							}
						},
						{
							iconCls: 'tr-grid-row-icon-sharing',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-sharing' + (!record.data.access.manage ? ' disabled' : '');
							},
							handler: function(grid, rowIndex) {
								var record = this.up('grid').store.getAt(rowIndex);

								if (record.data.access.manage) {
									Ext.Ajax.request({
										url:TR.conf.finals.ajax.path_api + 'sharing?type=patientTabularReport&id=' + record.data.id,
										disableCaching: false,
										method: 'GET',
										failure: function(r) {
											TR.util.mask.hideMask();
											alert(r.responseText);
										},
										success: function(r) {
											var sharing = Ext.decode(r.responseText),
												window = TR.app.SharingWindow(sharing, false);
											window.show();
										}
									});
								}
							}
						},
						{
							iconCls: 'tr-grid-row-icon-delete',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-overwrite' + (!record.data.access.update ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex);
									
								var message = TR.i18n.confirm_delete_favorite + '\n\n' + record.data.name;
								if (confirm(message)) {
									TR.util.mask.showMask(TR.cmp.caseBasedFavorite.window, TR.i18n.deleting + '...');
									var baseurl =  TR.conf.finals.ajax.casebasedfavorite_delete + "?id=" + record.data.id;
									selection = TR.cmp.caseBasedFavorite.grid.getSelectionModel().getSelection();
									Ext.Array.each(selection, function(item) {
										baseurl = Ext.String.urlAppend(baseurl, 'uids=' + item.data.id);
									});
									
									Ext.Ajax.request({
										url: baseurl,
										disableCaching: false,
										method: 'POST',
										success: function() {
											TR.store.caseBasedFavorite.loadStore();
											TR.util.mask.hideMask();
										}
									});  
								}
							}
						}
					]
				},
				{
					sortable: false,
					width: 2
				}
			],
			store: TR.store.caseBasedFavorite,
			bbar: [
				info,
				'->',
				prevButton,
				nextButton
			],
			listeners: {
				added: function() {
					TR.cmp.caseBasedFavorite.grid = this;
				},
				render: function() {
					var size = Math.floor((TR.cmp.region.center.getHeight() - 155) / TR.conf.layout.grid_row_height);
					this.store.pageSize = size;
					this.store.currentPage = 1;
					this.store.loadStore();

					TR.store.caseBasedFavorite.on('load', function() {
						if (this.isVisible()) {
							this.fireEvent('afterrender');
						}
					}, this);
				},
				afterrender: function() {
					var fn = function() {
						var editArray = Ext.query('.tooltip-favorite-edit'),
							deleteArray = Ext.query('.tooltip-favorite-delete'),
							el;

						for (var i = 0; i < editArray.length; i++) {
							el = editArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: TR.i18n.rename,
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}
						
						for (var i = 0; i < deleteArray.length; i++) {
							el = deleteArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: 'Delete', //i18n
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}
					};

					Ext.defer(fn, 100);
				},
				itemmouseenter: function(grid, record, item) {
					this.currentItem = Ext.get(item);
					this.currentItem.removeCls('x-grid-row-over');
				},
				select: function() {
					this.currentItem.removeCls('x-grid-row-selected');
				},
				selectionchange: function() {
					this.currentItem.removeCls('x-grid-row-focused');
				}
			}
		});
		
		favoriteWindow = Ext.create('Ext.window.Window', {
			title: TR.i18n.manage_favorites,
			bodyStyle: 'padding:5px; background-color:#fff',
			resizable: false,
			modal: true,
			width: windowWidth,
			destroyOnBlur: true,
			items: [
				{
					xtype: 'panel',
					layout: 'hbox',
					bodyStyle: 'border:0 none',
					items: [
						addButton,
						{
							height: 24,
							width: 1,
							style: 'width:1px; margin-left:5px; margin-right:5px; margin-top:1px',
							bodyStyle: 'border-left: 1px solid #aaa'
						},
						searchTextfield
					]
				},
				grid
			],
			listeners: {
				show: function(w) {
					TR.util.window.setAnchorPosition(w, TR.cmp.toolbar.favoritee);
				}
			}
		});

		return favoriteWindow;
	};
	
	TR.app.AggregateFavoriteWindow = function() {

		// Objects
		var NameWindow,

		// Instances
			nameWindow,
	
		// Components
			addButton,
			searchTextfield,
			grid,
			prevButton,
			nextButton,
			tbar,
			bbar,
			info,
			nameTextfield,
			createButton,
			updateButton,
			cancelButton,
			favoriteWindow,

		// Vars
			windowWidth = 500,
			windowCmpWidth = windowWidth - 22;

		TR.store.aggregateFavorite.on('load', function(store, records) {
			var pager = store.proxy.reader.jsonData.pager;

			info.setText( TR.i18n.page + ' ' + pager.currentPage + TR.i18n.of + ' ' + pager.pageCount );

			prevButton.enable();
			nextButton.enable();

			if (pager.currentPage === 1) {
				prevButton.disable();
			}

			if (pager.currentPage === pager.pageCount) {
				nextButton.disable();
			}
		});
		
		NameWindow = function(id) {
			var window;
			var record = TR.store.aggregateFavorite.getById(id);
			
			nameTextfield = Ext.create('Ext.form.field.Text', {
				height: 26,
				width: 371,
				fieldStyle: 'padding-left: 6px; border-radius: 1px; border-color: #bbb; font-size:11px',
				style: 'margin-bottom:0',
				emptyText: TR.i18n.favorite_name,
				value: id ? record.data.name : '',
				listeners: {
					afterrender: function() {
						this.focus();
					}
				}
			});
			
			createButton = Ext.create('Ext.button.Button', {
				text: TR.i18n.create,
				handler: function() {
					var name = nameTextfield.getValue();
					if (name) {
						
						// Validation
						
						if( !TR.state.aggregateReport.validation.objects() )
						{
							return;
						}
						
						// Save favorite
						
						TR.util.mask.showMask(TR.cmp.aggregateFavorite.window, TR.i18n.saving + '...');
						var p = TR.state.getParams();
						p.name = name;
						p.programId = Ext.getCmp('programCombobox').getValue(); 
						p.programStageId = TR.cmp.params.programStage.getValue();
						
						Ext.Ajax.request({
							url: TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.aggregatefavorite_validate,
							method: 'POST',
							disableCaching: false,
							params: {name:name},
							success: function(r) {
									var json = Ext.JSON.decode(r.responseText);
									if(json.response=='success'){
										Ext.Ajax.request({
											url: TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.aggregatefavorite_save,
											disableCaching: false,
											method: 'POST',
											params: p,
											success: function() {
												TR.store.aggregateFavorite.load({callback: function() {
													TR.util.mask.hideMask();
												}});
											}
										});  
									}
									else{
										TR.util.notification.error(TR.i18n.error, json.message);
										window.destroy();
										TR.util.mask.hideMask();
									}
								}
							});
					}
				}
			});
			
			updateButton = Ext.create('Ext.button.Button', {
				text: TR.i18n.update,
				handler: function() {
					var name = nameTextfield.getValue();

					if (id && name) {
					
						if (TR.store.aggregateFavorite.findExact('name', name) != -1) {
							return;
						}
						TR.util.mask.showMask(TR.cmp.aggregateFavorite.window, TR.i18n.renaming + '...');
						var r = TR.cmp.aggregateFavorite.grid.getSelectionModel().getSelection()[0];
						Ext.Ajax.request({
							url: TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.aggregatefavorite_rename,
							disableCaching: false,
							method: 'POST',
							params: {id: id, name: name},
							success: function() {
								TR.store.aggregateFavorite.loadStore();
								window.destroy();
								TR.util.mask.hideMask();
							}
						});
					}
				}
			});
			
			cancelButton = Ext.create('Ext.button.Button', {
				text: TR.i18n.cancel,
				handler: function() {
					window.destroy();
				}
			});
			
			window = Ext.create('Ext.window.Window', {
				title: id ? TR.i18n.rename_favorite : TR.i18n.create_new_favorite,
				bodyStyle: 'padding:2px; background:#fff',
				resizable: false,
				modal: true,
				items: nameTextfield,
				bbar: [
					cancelButton,
					'->',
					id ? updateButton : createButton
				],
				listeners: {
					show: function(w) {
						TR.util.window.setAnchorPosition(w, addButton);
					}
				}
			});

			return window;
		};

		addButton = Ext.create('Ext.button.Button', {
			text: TR.i18n.add_new,
			width: 75,
			height: 26,
			style: 'border-radius: 1px;',
			menu: {},
			handler: function() {
				nameWindow = new NameWindow(null, TR.i18n.create);
				nameWindow.show();
			}
		});

		searchTextfield = Ext.create('Ext.form.field.Text', {
			width: windowCmpWidth - addButton.width - 11,
			height: 26,
			fieldStyle: 'padding-right: 0; padding-left: 6px; border-radius: 1px; border-color: #bbb; font-size:11px',
			emptyText: TR.i18n.search_for_favorites,
			enableKeyEvents: true,
			currentValue: '',
			listeners: {
				keyup: function() {
					if (this.getValue() !== this.currentValue) {
						this.currentValue = this.getValue();

						var value = this.getValue();
						var url = ( value ) ? TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.aggregatefavorite_getall + "?query=" + value : null;
						var store = TR.store.aggregateFavorite;

						store.currentPage = 1;
						store.loadStore(url);
					}
				}
			}
		});

		prevButton = Ext.create('Ext.button.Button', {
			text: TR.i18n.prev,
			handler: function() {
				var value = searchTextfield.getValue();
				var url = ( value ) ? TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.aggregatefavorite_getall + "?query=" + value : null;
				var store = TR.store.aggregateFavorite;

				store.currentPage = store.currentPage <= 1 ? 1 : store.currentPage - 1;
				store.loadStore(url);
			}
		});

		nextButton = Ext.create('Ext.button.Button', {
			text: TR.i18n.next,
			handler: function() {
				var value = searchTextfield.getValue();
				var url = ( value ) ? TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.aggregatefavorite_getall + "?query=" + value : null;
				var store = TR.store.aggregateFavorite;

				store.currentPage = store.currentPage + 1;
				store.loadStore(url);
			}
		});

		info = Ext.create('Ext.form.Label', {
			cls: 'tr-label-info',
			width: 300,
			height: 22
		});
		
		grid = Ext.create('Ext.grid.Panel', {
			cls: 'tr-grid',
			scroll: false,
			hideHeaders: true,
			columns: [
				{
					dataIndex: 'name',
					sortable: false,
					width: windowCmpWidth - 88,
					renderer: function(value, metaData, record) {
						var fn = function() {
							var element = Ext.get(record.data.id);
							if (element) {
								element = element.parent('td');
								element.addClsOnOver('link');
								element.load = function() {
									favoriteWindow.hide();
									TR.util.crud.favorite.run( record.data.id );
								};
								element.dom.setAttribute('onclick', 'Ext.get(this).load();');
							}
						};

						Ext.defer(fn, 100);

						return '<div id="' + record.data.id + '">' + value + '</div>';
					}
				},
				{
					xtype: 'actioncolumn',
					sortable: false,
					width: 80,
					items: [
						{
							iconCls: 'tr-grid-row-icon-edit',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-edit' + (!record.data.access.update ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex);
								nameWindow = new NameWindow(record.data.id);
								nameWindow.show();
							}
						},
						{
							iconCls: 'tr-grid-row-icon-sharing',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-sharing' + (!record.data.access.manage ? ' disabled' : '');
							},
							handler: function(grid, rowIndex) {
								var record = this.up('grid').store.getAt(rowIndex);

								if (record.data.access.manage) {
									Ext.Ajax.request({
										url:TR.conf.finals.ajax.path_api + 'sharing?type=patientAggregateReport&id=' + record.data.id,
										disableCaching: false,
										method: 'GET',
										failure: function(r) {
											TR.util.mask.hideMask();
											alert(r.responseText);
										},
										success: function(r) {
											var sharing = Ext.decode(r.responseText),
												window = TR.app.SharingWindow(sharing, true);
											window.show();
										}
									});
								}
							}
						},
						{
							iconCls: 'tr-grid-row-icon-delete',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-overwrite' + (!record.data.access.update ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex);
									
								var message = TR.i18n.confirm_delete_favorite + '\n\n' + record.data.name;
								if (confirm(message)) {
									TR.util.mask.showMask(TR.cmp.aggregateFavorite.window, TR.i18n.deleting + '...');
									var baseurl =  TR.conf.finals.ajax.aggregatefavorite_delete + "?id=" + record.data.id;
									selection = TR.cmp.aggregateFavorite.grid.getSelectionModel().getSelection();
									Ext.Array.each(selection, function(item) {
										baseurl = Ext.String.urlAppend(baseurl, 'uids=' + item.data.id);
									});
									
									Ext.Ajax.request({
										url: baseurl,
										disableCaching: false,
										method: 'POST',
										success: function() {
											TR.store.aggregateFavorite.loadStore();
											TR.util.mask.hideMask();
										}
									});  
								}
							}
						}
					]
				},
				{
					sortable: false,
					width: 2
				}
			],
			store: TR.store.aggregateFavorite,
			bbar: [
				info,
				'->',
				prevButton,
				nextButton
			],
			listeners: {
				added: function() {
					TR.cmp.aggregateFavorite.grid = this;
				},
				render: function() {
					var size = Math.floor((TR.cmp.region.center.getHeight() - 155) / TR.conf.layout.grid_row_height);
					this.store.pageSize = size;
					this.store.currentPage = 1;
					this.store.loadStore();

					TR.store.aggregateFavorite.on('load', function() {
						if (this.isVisible()) {
							this.fireEvent('afterrender');
						}
					}, this);
				},
				afterrender: function() {
					var fn = function() {
						var editArray = Ext.query('.tooltip-favorite-edit'),
							deleteArray = Ext.query('.tooltip-favorite-delete'),
							el;

						for (var i = 0; i < editArray.length; i++) {
							el = editArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: TR.i18n.rename,
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}
						
						for (var i = 0; i < deleteArray.length; i++) {
							el = deleteArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: tr.i18n.delete,
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}
					};

					Ext.defer(fn, 100);
				},
				itemmouseenter: function(grid, record, item) {
					this.currentItem = Ext.get(item);
					this.currentItem.removeCls('x-grid-row-over');
				},
				select: function() {
					this.currentItem.removeCls('x-grid-row-selected');
				},
				selectionchange: function() {
					this.currentItem.removeCls('x-grid-row-focused');
				}
			}
		});
		
		favoriteWindow = Ext.create('Ext.window.Window', {
			title: TR.i18n.manage_favorites,
			bodyStyle: 'padding:5px; background-color:#fff',
			resizable: false,
			modal: true,
			width: windowWidth,
			destroyOnBlur: true,
			items: [
				{
					xtype: 'panel',
					layout: 'hbox',
					bodyStyle: 'border:0 none',
					items: [
						addButton,
						{
							height: 24,
							width: 1,
							style: 'width:1px; margin-left:5px; margin-right:5px; margin-top:1px',
							bodyStyle: 'border-left: 1px solid #aaa'
						},
						searchTextfield
					]
				},
				grid
			],
			listeners: {
				show: function(w) {
					TR.util.window.setAnchorPosition(w, TR.cmp.toolbar.favoritee);
				}
			}
		});

		return favoriteWindow;
	};
	
	TR.app.OptionsWindow = function() {
		var optionsWindow;
		
		var aggregateTypeField = Ext.create('Ext.form.RadioGroup', {
			id: 'aggregateType',
			fieldLabel: TR.i18n.aggregate_type,
			labelWidth: 135,
			columns: 3,
			vertical: true,
			items: [{
				boxLabel: TR.i18n.count,
				name: 'aggregateType',
				inputValue: 'count',
				checked: true
			}, 
			{
				boxLabel: TR.i18n.sum,
				name: 'aggregateType',
				inputValue: 'sum'
			}, 
			{
				boxLabel: TR.i18n.avg,
				name: 'aggregateType',
				inputValue: 'avg'
			}],
			listeners: {
				change : function(thisFormField, newValue, oldValue, eOpts) {
				  var opt = newValue.aggregateType[0];
				  
				  if( opt==oldValue.aggregateType && newValue.aggregateType.length > 1){
					opt = newValue.aggregateType[1];
				  }
				  
				  if (opt=='sum' || opt=='avg') {
					Ext.getCmp('deSumCbx').enable();
				  }
				  else  if (opt=='count'){
					Ext.getCmp('deSumCbx').disable();
				  }
				}
			}
		});
		
		var deSumField = Ext.create('Ext.form.field.ComboBox', {
			cls: 'tr-combo',
			id: 'deSumCbx',
			disabled: true,
			fieldLabel: TR.i18n.sum_avg_of,
			labelWidth: 135,
			emptyText: TR.i18n.please_select,
			queryMode: 'local',
			editable: true,
			typeAhead: true,
			valueField: 'id',
			displayField: 'name',
			width: TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor - 40,
			store: TR.store.aggregateDataelement,
			listeners: {
				added: function() {
					TR.cmp.settings.aggregateDataelement = this;
				}
			}
		});
		
		var ouModeField = Ext.create('Ext.form.field.ComboBox', {
			cls: 'tr-combo',
			id: 'ouModeCombobox',
			fieldLabel: TR.i18n.use_data_from_level,
			labelWidth: 135,
			emptyText: TR.i18n.please_select,
			queryMode: 'local',
			editable: false,
			valueField: 'value',
			displayField: 'name',
			width: TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor - 40,
			store:  new Ext.data.ArrayStore({
				fields: ['value', 'name'],
				data: [['', TR.i18n.all], ['DESCENDANTS', TR.i18n.children_only],
					['CHILDREN', TR.i18n.immediate_children], ['SELECTED', TR.i18n.selected]],
			}),
			value: '',
			listeners: {
				added: function() {
					TR.cmp.settings.ouMode = this;
				}
			}
		});
		
		var limitOptionField = Ext.create('Ext.form.field.Number',{
			id: 'limitOption',
			fieldLabel: TR.i18n.limit_records,
			labelSeparator: '',
			labelWidth: 135,
			editable: true,
			allowBlank:true,
			width: TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor - 30,
			minValue: 1,
			listeners: {
				added: function() {
					TR.cmp.settings.limitOption = this;
				}
			}
		});
						
		optionsWindow = Ext.create('Ext.window.Window', {
			title: TR.i18n.options,
			bodyStyle: 'background-color:#fff; padding:8px 8px 8px',
			closeAction: 'hide',
			autoShow: true,
			modal: true,
			resizable: false,
			hideOnBlur: true,
			items: [
				{
					xtype: 'fieldset',
					layout: 'anchor',
					collapsible: false,
					collapsed: false,
					defaults: {
							anchor: '100%',
							labelStyle: 'padding-left:4px;'
					},
					items: [
						aggregateTypeField,
						deSumField,
						ouModeField,
						limitOptionField
					]
				}
			],
			bbar: [
				'->',
				{
					text: TR.i18n.hide,
					handler: function() {
						optionsWindow.hide();
					}
				},
				{
					text: '<b>' + TR.i18n.update + '</b>',
					handler: function() {
						TR.exe.execute();
						optionsWindow.hide();
					}
				}
			],
			listeners: {
				show: function(w) {
					TR.util.window.setAnchorPosition(w, TR.cmp.toolbar.favoritee);
				}
			}
		});
		
		return optionsWindow;
	};
		
	TR.app.SharingWindow = function(sharing, isAggregate) {

		// Objects
		var UserGroupRow,

		// Functions
			getBody,

		// Components
			userGroupStore,
			userGroupField,
			userGroupButton,
			userGroupRowContainer,
			publicGroup,
			window;

		UserGroupRow = function(obj, isPublicAccess, disallowPublicAccess) {
			var getData,
				store,
				getItems,
				combo,
				getAccess,
				panel;

			getData = function() {
				var data = [
					{id: 'r-------', name: TR.i18n.can_view}, //i18n
					{id: 'rw------', name: TR.i18n.can_edit_and_view}
				];

				if (isPublicAccess) {
					data.unshift({id: '-------', name: TR.i18n.none});
				}

				return data;
			}

			store = Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				data: getData()
			});

			getItems = function() {
				var items = [];

				combo = Ext.create('Ext.form.field.ComboBox', {
					fieldLabel: isPublicAccess ? TR.i18n.public_access : obj.name, //i18n
					labelStyle: 'color:#333',
					cls: 'tr-combo',
					fieldStyle: 'padding-left:5px',
					width: 380,
					labelWidth: 250,
					queryMode: 'local',
					valueField: 'id',
					displayField: 'name',
					labelSeparator: null,
					editable: false,
					disabled: !!disallowPublicAccess,
					value: obj.access || 'rw------',
					store: store
				});

				items.push(combo);

				if (!isPublicAccess) {
					items.push(Ext.create('Ext.Img', {
						src: 'images/grid-delete_16.png',
						style: 'margin-top:2px; margin-left:7px',
						overCls: 'pointer',
						width: 16,
						height: 16,
						listeners: {
							render: function(i) {
								i.getEl().on('click', function(e) {
									i.up('panel').destroy();
									window.doLayout();
								});
							}
						}
					}));
				}

				return items;
			};

			getAccess = function() {
				return {
					id: obj.id,
					name: obj.name,
					access: combo.getValue()
				};
			};

			panel = Ext.create('Ext.panel.Panel', {
				layout: 'column',
				bodyStyle: 'border:0 none',
				getAccess: getAccess,
				items: getItems()
			});

			return panel;
		};

		getBody = function() {
			var body = {
				object: {
					id: sharing.object.id,
					name: sharing.object.name,
					publicAccess: publicGroup.down('combobox').getValue(),
					user: {
						id: TR.init.system.user.id,
						name: TR.init.system.user.name
					}
				}
			};

			if (userGroupRowContainer.items.items.length > 1) {
				body.object.userGroupAccesses = [];
				for (var i = 1, item; i < userGroupRowContainer.items.items.length; i++) {
					item = userGroupRowContainer.items.items[i];
					body.object.userGroupAccesses.push(item.getAccess());
				}
			}

			return body;
		};

		// Initialize
		userGroupStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: TR.init.path_api + '/sharing/search',
				reader: {
					type: 'json',
					root: 'userGroups'
				}
			}
		});

		userGroupField = Ext.create('Ext.form.field.ComboBox', {
			valueField: 'id',
			displayField: 'name',
			emptyText: 'Search for user groups', //i18n
			queryParam: 'key',
			queryDelay: 200,
			minChars: 1,
			hideTrigger: true,
			fieldStyle: 'height:26px; padding-left:6px; border-radius:1px; font-size:11px',
			style: 'margin-bottom:5px',
			width: 380,
			store: userGroupStore,
			listeners: {
				beforeselect: function(cb) { // beforeselect instead of select, fires regardless of currently selected item
					userGroupButton.enable();
				},
				afterrender: function(cb) {
					cb.inputEl.on('keyup', function() {
						userGroupButton.disable();
					});
				}
			}
		});

		userGroupButton = Ext.create('Ext.button.Button', {
			text: '+',
			style: 'margin-left:2px; padding-right:4px; padding-left:4px; border-radius:1px',
			disabled: true,
			height: 26,
			handler: function(b) {
				userGroupRowContainer.add(UserGroupRow({
					id: userGroupField.getValue(),
					name: userGroupField.getRawValue(),
					access: 'r-------'
				}));

				userGroupField.clearValue();
				b.disable();
			}
		});

		userGroupRowContainer = Ext.create('Ext.container.Container', {
			bodyStyle: 'border:0 none'
		});

		publicGroup = userGroupRowContainer.add(UserGroupRow({
			id: sharing.object.id,
			name: sharing.object.name,
			access: sharing.object.publicAccess
		}, true, !sharing.meta.allowPublicAccess));

		getURL = function(objectId) {
			if(isAggregate){
				return TR.conf.finals.ajax.path_api + 'sharing?type=patientAggregateReport&id=' + objectId;
			}
			return TR.conf.finals.ajax.path_api + 'sharing?type=patientTabularReport&id=' + objectId;
		};
		
		if (Ext.isArray(sharing.object.userGroupAccesses)) {
			for (var i = 0, userGroupRow; i < sharing.object.userGroupAccesses.length; i++) {
				userGroupRow = UserGroupRow(sharing.object.userGroupAccesses[i]);
				userGroupRowContainer.add(userGroupRow);
			}
		}

		window = Ext.create('Ext.window.Window', {
			title: 'Sharing layout',
			bodyStyle: 'padding:6px 6px 0px; background-color:#fff',
			resizable: false,
			modal: true,
			destroyOnBlur: true,
			items: [
				{
					html: sharing.object.name,
					bodyStyle: 'border:0 none; font-weight:bold; color:#333',
					style: 'margin-bottom:8px'
				},
				{
					xtype: 'container',
					layout: 'column',
					bodyStyle: 'border:0 none',
					items: [
						userGroupField,
						userGroupButton
					]
				},
				userGroupRowContainer
			],
			bbar: [
				'->',
				{
					text: 'Save',
					handler: function() {
						Ext.Ajax.request({
							url: getURL(sharing.object.id),
							disableCaching: false,
							method: 'POST',
							headers: {
								'Content-Type': 'application/json'
							},
							params: Ext.encode(getBody())
						});

						window.destroy();
					}
				}
			],
			listeners: {
				show: function(w) {
					if(isAggregate){
						var pos = TR.cmp.aggregateFavorite.window.getPosition();
						w.setPosition(pos[0] + 5, pos[1] + 5);
						TR.cmp.aggregateFavorite.window.destroyOnBlur = false;
					}
					else {
						var pos = TR.cmp.caseBasedFavorite.window.getPosition();
						w.setPosition(pos[0] + 5, pos[1] + 5);
						TR.cmp.caseBasedFavorite.window.destroyOnBlur = false;
					}
				},
				destroy: function() {
					if(isAggregate){
						TR.cmp.aggregateFavorite.window.destroyOnBlur = true;
					}
					else{
						TR.cmp.caseBasedFavorite.window.destroyOnBlur = true;
					}
				}
			}
		});

		return window;
	};
	
	TR.app.LayoutWindow = function() {
		var row,
			rowStore,
			col,
			colStore,
			filter,
			filterStore,
			value,

			getStore,
			getStoreKeys,
			getCmpHeight,
			getSetup,

			selectPanel,
			window,

			margin = 2,
			defaultWidth = 160,
			defaultHeight = 50,
			maxHeight = 200,
		
		getStore = function(data) {
			var config = {};

			config.fields = ['id', 'name'];

			if (data) {
				config.data = data;
			}
			return Ext.create('Ext.data.Store', config);
		};

		getStoreKeys = function(store) {
			var keys = [],
				items = store.data.items;

			if (items) {
				for (var i = 0; i < items.length; i++) {
					keys.push(items[i].data.id);
				}
			}

			return keys;
		};

		rowStore = getStore();
		rowStore.add({id: 1, name: TR.i18n.organisation_units});
		colStore = getStore();
		colStore.add({id: 2, name: TR.i18n.periods});
		filterStore = getStore();
		filterStore.add({id: 3, name: TR.i18n.data});
		
		getCmpHeight = function() {
			var size = 20,
				expansion = 10,
				height = defaultHeight,
				diff;

			if (size > 10) {
				diff = size - 10;
				height += (diff * expansion);
			}

			height = height > maxHeight ? maxHeight : height;

			return height;
		};

		row = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'tr-toolbar-multiselect-leftright',
			id: 'positionRowCbx',
			width: defaultWidth,
			height: getCmpHeight(),
			style: 'margin-bottom:0px',
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'layoutDD',
			dropGroup: 'layoutDD',
			store: rowStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: TR.i18n.row,
					cls: 'tr-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.boundList.on('itemdblclick', function(view, record) {
						ms.store.remove(record);
					});

					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		col = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'tr-toolbar-multiselect-leftright',
			id: 'positionColCbx',
			width: defaultWidth,
			height: getCmpHeight(),
			style: 'margin-bottom:' + margin + 'px',
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'layoutDD',
			dropGroup: 'layoutDD',
			store: colStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: TR.i18n.column,
					cls: 'tr-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.boundList.on('itemdblclick', function(view, record) {
						ms.store.remove(record);
					});

					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		filter = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'tr-toolbar-multiselect-leftright',
			id: 'positionFilterCbx',
			width: defaultWidth,
			height: getCmpHeight(),
			style: 'margin-right:' + margin + 'px; margin-bottom:' + margin + 'px',
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'layoutDD',
			dropGroup: 'layoutDD',
			store: filterStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: TR.i18n.filter,
					cls: 'tr-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.boundList.on('itemdblclick', function(view, record) {
						ms.store.remove(record);
					});

					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		selectPanel = Ext.create('Ext.panel.Panel', {
			bodyStyle: 'border:0 none',
			items: [
				{
					layout: 'column',
					bodyStyle: 'border:0 none',
					items: [
						filter,
						col
					]
				},
				{
					layout: 'column',
					bodyStyle: 'border:0 none',
					items: [
						row
					]
				}
			]
		});

		getSetup = function() {
			return {
				col: getStoreKeys(colStore),
				row: getStoreKeys(rowStore),
				filter: getStoreKeys(filterStore)
			};
		};

		window = Ext.create('Ext.window.Window', {
			title: TR.i18n.table_layout,
			bodyStyle: 'background-color:#fff; padding:2px',
			closeAction: 'hide',
			autoShow: true,
			modal: true,
			resizable: false,
			getSetup: getSetup,
			rowStore: rowStore,
			colStore: colStore,
			filterStore: filterStore,
			hideOnBlur: true,
			items: {
				layout: 'column',
				bodyStyle: 'border:0 none',
				items: [
					selectPanel
				]
			},
			bbar: [
				'->',
				{
					text: TR.i18n.hide,
					listeners: {
						added: function(b) {
							b.on('click', function() {
								window.hide();
							});
						}
					}
				},
				{
					text: '<b>' + TR.i18n.update + '</b>',
					handler: function() {
						TR.exe.execute();
						window.hide();
					}
				}
			],
			listeners: {
				show: function(w) {
					TR.util.window.setAnchorPosition(w, TR.cmp.toolbar.favoritee);
				}
			}
		});

		return window;
	};
	
	
    TR.viewport = Ext.create('Ext.container.Viewport', {
        layout: 'border',
        renderTo: Ext.getBody(),
        isrendered: false,
		items: [
            {
                region: 'west',
                preventHeader: true,
                collapsible: true,
                collapseMode: 'mini',
                items: [
					{
						xtype: 'toolbar',
						width: TR.conf.layout.west_fieldset_width + 20,
						style: 'padding:2px 0 0 2px; border:0 none; border-bottom:1px solid #ccc; background-color:transparent',
						items: [
						{
							xtype: 'panel',
							bodyStyle: 'border-style:none; background-color:transparent; padding:2px',
							items: [
								Ext.create('Ext.form.Panel', {
								bodyStyle: 'border-style:none; background-color:transparent; padding:3px 30px 0 8px',
								items: [
								{
									xtype: 'radiogroup',
									id: 'reportTypeGroup',
									fieldLabel: TR.i18n.report_type,
									labelStyle: 'font-weight:bold',
									columns: 2,
									vertical: true,
									items: [
									{
										boxLabel: TR.i18n.case_based_report,
										name: 'reportType',
										inputValue: 'true',
										checked: true,
										listeners: {
											change: function (cb, nv, ov) {
												if(nv)
												{
													// for case-based report
													Ext.getCmp('limitOption').setVisible(false);
													Ext.getCmp('aggregateType').setVisible(false);
													Ext.getCmp('downloadCvsIcon').setVisible(false);
													Ext.getCmp('aggregateFavoriteBtn').setVisible(false);
													Ext.getCmp('deSumCbx').setVisible(false);
													Ext.getCmp('caseBasedFavoriteBtn').setVisible(true);
													Ext.getCmp('relativePeriodsDiv').setVisible(false);
													Ext.getCmp('filterPanel').setHeight(253);
													
												}
											}
										}
									}, 
									{
										boxLabel: TR.i18n.aggregated_report,
										name: 'reportType',
										inputValue: 'false',
										listeners: {
											change: function (cb, nv, ov) {
												if(nv)
												{
													// For aggregate report
													Ext.getCmp('limitOption').setVisible(true);
													Ext.getCmp('aggregateType').setVisible(true);
													Ext.getCmp('downloadCvsIcon').setVisible(true);
													Ext.getCmp('aggregateFavoriteBtn').setVisible(true);
													Ext.getCmp('deSumCbx').setVisible(true);
													Ext.getCmp('caseBasedFavoriteBtn').setVisible(false);
													
													Ext.getCmp('relativePeriodsDiv').setVisible(true);
													Ext.getCmp('filterPanel').setHeight(223);
													Ext.getCmp('dateRangeDiv').expand();
												}
											}
										}
									}]
								}]
							}),
							{ bodyStyle: 'padding:1px 0; border-style:none;	background-color:transparent' },
							{
								xtype: 'panel',
								layout: 'column',
								bodyStyle: 'border-style:none; background-color:transparent; padding:4px 0 0 8px',
								width: TR.conf.layout.west_fieldset_width + 50,
								items:[
								{
									xtype: 'combobox',
									cls: 'tr-combo',
									name: TR.init.system.programs,
									id: 'programCombobox',
									style: 'margin-bottom:2px',
									fieldLabel: TR.i18n.program,
									labelStyle: 'font-weight:bold; margin-bottom:2px',
									labelAlign: 'top',
									emptyText: TR.i18n.please_select,
									queryMode: 'local',
									editable: false,
									valueField: 'id',
									displayField: 'name',
									width: TR.conf.layout.west_fieldset_width / 2 - 3,
									store: TR.store.program,
									listeners: {
										added: function() {
											TR.cmp.settings.program = this;
										},
										select: function(cb) {
											var pid = cb.getValue();
											TR.store.programStageSection.loadData([],false);
										
											// Program-stage
											
											var storeProgramStage = TR.store.programStage;
											storeProgramStage.parent = pid;
											
											var url = storeProgramStage.getProxy().url + pid  + ".json";
											storeProgramStage.load({url:url});
											
											TR.store.dataelement.available.removeAll();
											TR.store.dataelement.selected.removeAll();
											TR.store.dataelement.isLoadFromFavorite = false;
											
											// Filter value fields
											
											Ext.getCmp('filterPanel').removeAll();
										}
									}
								},
								{
									xtype: 'combobox',
									cls: 'tr-combo',
									id:'programStageCombobox',
									style: 'margin-left:2px; margin-bottom:2px',
									fieldLabel: TR.i18n.program_stage,
									labelStyle: 'font-weight:bold; margin-bottom:2px',
									labelAlign: 'top',
									emptyText: TR.i18n.please_select,
									queryMode: 'local',
									editable: false,
									valueField: 'id',
									displayField: 'name',
									width:  TR.conf.layout.west_fieldset_width / 2 - 3,
									store: TR.store.programStage,
									listeners: {
										added: function() {
											TR.cmp.params.programStage = this;
										},  
										select: function(cb) {
											var psid = cb.getValue();
											
											// Get section from the selected program stage
											
											var sectionStore = TR.store.programStageSection;
											sectionStore.loadData([],false);
											sectionStore.parent = psid;
											
											if (TR.util.store.containsParent(sectionStore)) {
												TR.util.store.loadFromStorage(sectionStore);
											}
											else {
												sectionStore.load({params: {programStageId: psid}});
											}
											
											// Get data element from the selected program stage
											
											var store = TR.store.dataelement.available;
											TR.store.dataelement.selected.loadData([],false);
											store.parent = psid;
											
											if (TR.util.store.containsParent(store)) {
												TR.util.store.loadFromStorage(store);
												TR.util.multiselect.filterAvailable(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected);
											}
											else {
												store.load({params: {programStageId: psid}});
											}
											
											// Filter value fields
											Ext.getCmp('filterPanel').removeAll();
										},
										specialKey :  function(field,e) {
											if(e.getKey() === e.TAB){
												Ext.getCmp('dateRangeDiv').expand();
											}
										}
									}
								}
								]
							}]
						}]
					},                            
					{
						xtype: 'panel',
                        bodyStyle: 'border:0 none; padding:2px 2px 0',
                        items: [
							{
								xtype: 'panel',
								layout: 'accordion',
								activeOnTop: true,
								cls: 'tr-accordion',
								bodyStyle: 'border:0 none; margin-bottom:2px',
								height: 554,
								items: [
							
									// DATE-RANGE
									{
										title: '<div style="height:17px; background-image:url(images/period.png); background-repeat:no-repeat; padding-left:20px">' + TR.i18n.period_range + '</div>',
										id: 'dateRangeDiv',
										hideCollapseTool: true,
										autoScroll: true,
										bodyStyle: 'padding:3px 4px',
										items: [
											{
												xtype: 'datefield',
												cls: 'tr-textfield-alt1',
												id: 'startDate',
												style: 'margin-bottom:2px',
												fieldLabel: TR.i18n.start_date,
												labelStyle: 'position:relative; top:3px',
												labelWidth: 90,
												editable: true,
												allowBlank:true,
												invalidText: TR.i18n.the_date_is_not_valid,
												width: TR.conf.layout.west_fieldset_width + 7,
												format: TR.i18n.format_date,
												value: new Date((new Date()).setMonth((new Date()).getMonth()-3)),
												maxValue: new Date(),
												listeners: {
													added: function() {
														TR.cmp.settings.startDate = this;
													},
													change:function(){
														var startDate =  TR.cmp.settings.startDate.getValue();
														Ext.getCmp('endDate').setMinValue(startDate);
													}
												}
											},
											{
												xtype: 'datefield',
												cls: 'tr-textfield-alt1',
												id: 'endDate',
												fieldLabel: TR.i18n.end_date,
												labelStyle: 'position:relative; top:3px',
												labelWidth: 90,
												editable: true,
												allowBlank:true,
												invalidText: TR.i18n.the_date_is_not_valid,
												width: TR.conf.layout.west_fieldset_width + 7,
												format: TR.i18n.format_date,
												value: new Date(),
												minValue: new Date((new Date()).setMonth((new Date()).getMonth()-3)),
												listeners: {
													added: function() {
														TR.cmp.settings.endDate = this;
													},
													change:function(){
														var endDate =  TR.cmp.settings.endDate.getValue();
														Ext.getCmp('startDate').setMaxValue( endDate );
													},
													specialKey :  function(field,e) {
														if(e.getKey() === e.TAB){
															Ext.getCmp('dateRangeDiv').collapse();
															if(Ext.getCmp('reportTypeGroup').getValue().reportType=='true')
															{
																Ext.getCmp('orgunitDiv').expand();
															}
															else
															{
																Ext.getCmp('relativePeriodsDiv').expand();
															}
														}
													}
												}
											}
										]
									},
									
									// RELATIVE PERIODS
									{
										title: '<div style="height:17px; background-image:url(images/period.png); background-repeat:no-repeat; padding-left:20px">' + TR.i18n.relative_periods + '</div>',
										id: 'relativePeriodsDiv',
										hideCollapseTool: true,
										items: [
											{
												xtype: 'panel',
												layout: 'column',
												bodyStyle: 'border-style:none',
												items: [
													{
														xtype: 'panel',
														columnWidth: 0.32,
														bodyStyle: 'border-style:none; padding:0 0 0 6px',
														defaults: {
															labelSeparator: '',
															style: 'margin-bottom:2px',
															listeners: {
																added: function(chb) {
																	if (chb.xtype === 'checkbox') {
																		TR.cmp.params.relativeperiod.checkbox.push(chb);
																	}
																}
															}
														},
														items: [
															{
																xtype: 'label',
																text: TR.i18n.weeks,
																cls: 'tr-label-period-heading'
															},
															{
																xtype: 'checkbox',
																paramName: 'LAST_WEEK',
																boxLabel: TR.i18n.last_week
															},
															{
																xtype: 'checkbox',
																paramName: 'LAST_4_WEEKS',
																boxLabel: TR.i18n.last_4_weeks
															},
															{
																xtype: 'checkbox',
																paramName: 'LAST_12_WEEKS',
																boxLabel: TR.i18n.last_12_weeks
															},
															{
																xtype: 'checkbox',
																paramName: 'LAST_52_WEEKS',
																boxLabel: TR.i18n.last_52_weeks
															}
														]
													},
													{
														xtype: 'panel',
														layout: 'anchor',
														columnWidth: 0.32,
														bodyStyle: 'border-style:none; padding:0 0 0 10px',
														defaults: {
															style: 'margin-bottom:2px',
															labelSeparator: '',
															listeners: {
																added: function(chb) {
																	if (chb.xtype === 'checkbox') {
																		TR.cmp.params.relativeperiod.checkbox.push(chb);
																	}
																}
															}
														},
														items: [
															{
																xtype: 'label',
																text: TR.i18n.months,
																cls: 'tr-label-period-heading'
															},
															{
																xtype: 'checkbox',
																paramName: 'LAST_MONTH',
																boxLabel: TR.i18n.last_month
															},
															{
																xtype: 'checkbox',
																paramName: 'LAST_3_MONTHS',
																boxLabel: TR.i18n.last_3_months
															},
															{
																xtype: 'checkbox',
																paramName: 'LAST_12_MONTHS',
																boxLabel: TR.i18n.last_12_months
															},
															{
																xtype: 'checkbox',
																paramName: 'MONTHS_LAST_YEAR',
																boxLabel: TR.i18n.months_last_year
															},
															{
																xtype: 'checkbox',
																paramName: 'MONTHS_THIS_YEAR',
																boxLabel: TR.i18n.months_this_year
															}
														]
													},
													{
														xtype: 'panel',
														layout: 'anchor',
														columnWidth: 0.32,
														bodyStyle: 'border-style:none; padding:0 0 0 10px',
														defaults: {
															style: 'margin-bottom:2px',
															labelSeparator: '',
															listeners: {
																added: function(chb) {
																	if (chb.xtype === 'checkbox') {
																		TR.cmp.params.relativeperiod.checkbox.push(chb);
																	}
																}
															}
														},
														items: [
															{
																xtype: 'label',
																text: TR.i18n.quarters,
																cls: 'tr-label-period-heading'
															},
															{
																xtype: 'checkbox',
																paramName: 'LAST_QUARTER',
																boxLabel: TR.i18n.last_quarter
															},
															{
																xtype: 'checkbox',
																paramName: 'LAST_4_QUARTERS',
																boxLabel: TR.i18n.last_4_quarters
															},
															{
																xtype: 'checkbox',
																paramName: 'QUARTERS_LAST_YEAR',
																boxLabel: TR.i18n.quarters_last_year
															},
															{
																xtype: 'checkbox',
																paramName: 'QUARTERS_THIS_YEAR',
																boxLabel: TR.i18n.quarters_this_year
															}
														]
													}
												]
											},
											{
												xtype: 'panel',
												layout: 'column',
												bodyStyle: 'border-style:none; padding-top:6px',
												items: [
												{
													xtype: 'panel',
													columnWidth: 0.32,
													bodyStyle: 'border-style:none; padding:0 0 0 6px',
													defaults: {
														labelSeparator: '',
														style: 'margin-bottom:2px',
														listeners: {
															added: function(chb) {
																if (chb.xtype === 'checkbox') {
																	TR.cmp.params.relativeperiod.checkbox.push(chb);
																}
															}
														}
													},
													items: [
														{
															xtype: 'label',
															text: TR.i18n.six_months,
															cls: 'tr-label-period-heading'
														},
														{
															xtype: 'checkbox',
															paramName: 'LAST_SIX_MONTH',
															boxLabel: TR.i18n.last_six_month
														},
														{
															xtype: 'checkbox',
															paramName: 'LAST_2_SIXMONTHS',
															boxLabel: TR.i18n.last_two_six_month
														}
													]
												},
												{
													xtype: 'panel',
													columnWidth: 0.32,
													bodyStyle: 'border-style:none; padding:0 0 0 10px',
													defaults: {
														labelSeparator: '',
														style: 'margin-bottom:2px',
														listeners: {
															added: function(chb) {
																if (chb.xtype === 'checkbox') {
																	TR.cmp.params.relativeperiod.checkbox.push(chb);
																}
															}
														}
													},
													items: [
														{
															xtype: 'label',
															text: TR.i18n.bimonths,
															cls: 'tr-label-period-heading'
														},
														{
															xtype: 'checkbox',
															paramName: 'LAST_BIMONTH',
															boxLabel: TR.i18n.last_bimonth
														},
														{
															xtype: 'checkbox',
															paramName: 'LAST_6_BIMONTHS',
															boxLabel: TR.i18n.last_6_bimonths
														}
													]
												},
												{
													xtype: 'panel',
													columnWidth: 0.32,
													bodyStyle: 'border-style:none; padding:0 0 0 8px',
													defaults: {
														labelSeparator: '',
														style: 'margin-bottom:2px',
														listeners: {
															added: function(chb) {
																if (chb.xtype === 'checkbox') {
																	TR.cmp.params.relativeperiod.checkbox.push(chb);
																}
															}
														}
													},
													items: [
														{
															xtype: 'panel',
															layout: 'anchor',
															bodyStyle: 'border-style:none; padding:0 0 0 5px',
															defaults: {
																labelSeparator: '',
																style: 'margin-bottom:2px',
																listeners: {
																	added: function(chb) {
																		if (chb.xtype === 'checkbox') {
																			TR.cmp.params.relativeperiod.checkbox.push(chb);
																		}
																	}
																}
															},
															items: [
																{
																	xtype: 'label',
																	text: TR.i18n.years,
																	cls: 'tr-label-period-heading'
																},
																{
																	xtype: 'checkbox',
																	paramName: 'THIS_YEAR',
																	boxLabel: TR.i18n.this_year
																},
																{
																	xtype: 'checkbox',
																	paramName: 'LAST_YEAR',
																	boxLabel: TR.i18n.last_year
																},
																{
																	xtype: 'checkbox',
																	paramName: 'LAST_5_YEARS',
																	boxLabel: TR.i18n.last_5_years
																}
															]
														}
													]
												}
												]
											}
										], 
										listeners: {
											added: function() {
												TR.cmp.params.relativeperiod.panel = this;
											}
										}
									},
									
									// ORGANISATION UNIT
									{
										title: '<div style="height:17px;background-image:url(images/organisationunit.png); background-repeat:no-repeat; padding-left:20px">' + TR.i18n.organisation_units + '</div>',
										id: 'orgunitDiv',
										hideCollapseTool: true,
										items: [
											{
												xtype: 'combobox',
												cls: 'tr-combo',
												name: TR.init.system.orgunitGroup,
												id: 'orgGroupCombobox',
												emptyText: TR.i18n.please_select,
												hidden: true,
												queryMode: 'local',
												editable: false,
												valueField: 'id',
												displayField: 'name',
												fieldLabel: TR.i18n.orgunit_groups,
												labelWidth: 135,
												emptyText: TR.i18n.please_select,
												width: TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor + 28,
												store: TR.store.orgunitGroup,
												listeners: {
													added: function() {
														TR.cmp.settings.orgunitGroup = this;
													}
												}
											},
											{
												layout: 'column',
												bodyStyle: 'border:0 none; padding-bottom:3px',
												style: 'margin-top:2px; margin-left:2px',
												items: [
													{
														xtype: 'checkbox',
														id: 'userOrgunit',
														columnWidth: 0.5,
														boxLabel: TR.i18n.user_orgunit,
														labelWidth: TR.conf.layout.form_label_width,
														handler: function(chb, checked) {
															TR.cmp.params.organisationunit.treepanel.xable(checked, TR.cmp.aggregateFavorite.userorganisationunitchildren.getValue());
															TR.state.orgunitIds = [];
														},
														listeners: {
															added: function() {
																TR.cmp.aggregateFavorite.userorganisationunit = this;
															}
														}
													},
													{
														xtype: 'checkbox',
														id: 'userOrgunitChildren',
														columnWidth: 0.5,
														boxLabel: TR.i18n.user_orgunit_children,
														labelWidth: TR.conf.layout.form_label_width,
														handler: function(chb, checked) {
															TR.cmp.params.organisationunit.treepanel.xable(checked, TR.cmp.aggregateFavorite.userorganisationunit.getValue());
														},
														listeners: {
															added: function() {
																TR.cmp.aggregateFavorite.userorganisationunitchildren = this;
															},
															handler: function(chb, checked) {
																TR.cmp.params.organisationunit.toolbar.xable(checked, TR.cmp.aggregateFavorite.userorganisationunitchildren.getValue());
																TR.cmp.params.organisationunit.treepanel.xable(checked, TR.cmp.aggregateFavorite.userorganisationunitchildren.getValue());
																TR.state.orgunitIds = [];
															},
														}
													}
												]
											},
											{
												xtype: 'treepanel',
												id: 'treeOrg',
												cls: 'tr-tree',
												width: TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor + 28,
												displayField: 'name',
												rootVisible: false,
												autoScroll: true,
												multiSelect: true,
												rendered: false,
												selectRootIf: function() {
													if (this.getSelectionModel().getSelection().length < 1) {
														var node = this.getRootNode().findChild('id', TR.init.system.rootnodes[0].id, true);
														if (this.rendered) {
															this.getSelectionModel().select(node);
														}
														return node;
													}
												},
												numberOfRecords: 0,
												recordsToSelect: [],
												multipleSelectIf: function() {
													if (this.recordsToSelect.length === this.numberOfRecords) {
														this.getSelectionModel().select(this.recordsToSelect);
														TR.state.orgunitIds = [];
														for( var i in this.recordsToSelect){
															TR.state.orgunitIds.push( this.recordsToSelect[i].data.id );
														}
														this.recordsToSelect = [];
														this.numberOfRecords = 0;
													}
												},
												multipleExpand: function(id, path) {
													this.expandPath('/' + TR.conf.finals.root.id + path, 'id', '/', function() {
														var record = this.getRootNode().findChild('id', id, true);
														this.recordsToSelect.push(record);
														this.multipleSelectIf();
													}, this);
												},
												select: function(url, params) {
													if (!params) {
														params = {};
													}
													Ext.Ajax.request({
														url: url,
														disableCaching: false,
														method: 'GET',
														params: params,
														scope: this,
														success: function(r) {
															var a = Ext.JSON.decode(r.responseText).organisationUnits;
															this.numberOfRecords = a.length;
															for (var i = 0; i < a.length; i++) {
																this.multipleExpand(a[i].id, a[i].path);
															}
														}
													});
												},
												selectByGroup: function(id) {
													if (id) {
														var url = TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.organisationunit_getbygroup,
															params = {id: id};
														this.select(url, params);
													}
												},
												selectByLevel: function(level) {
													if (level) {
														var url = TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.organisationunit_getbylevel,
															params = {level: level};
														this.select(url, params);
													}
												},
												selectByIds: function(ids) {
													if (ids) {
														var url = TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.organisationunit_getbyids;
														Ext.Array.each(ids, function(item) {
															url = Ext.String.urlAppend(url, 'ids=' + item);
														});
														if (!this.rendered) {
															TR.cmp.params.organisationunit.panel.expand();
														}
														this.select(url);
													}
												},
												store: Ext.create('Ext.data.TreeStore', {
													fields: ['id', 'name'],
													proxy: {
														type: 'rest',
														format: 'json',
														noCache: false,
														extraParams: {
															links: 'false'
														},	
														url: TR.conf.finals.ajax.path_api + 'organisationUnits',
														reader: {
															type: 'json',
															root: 'children'
														}
													},
													sorters: [{
														property: 'name',
														direction: 'ASC'
													}],
													root: {
														id: TR.conf.finals.root.id,
														expanded: true,
														children: TR.init.system.rootnodes
													},
													listeners: {
														load: function(store, node, records) {
															Ext.Array.each(records, function(record) {
																record.set('leaf', !record.raw.hasChildren);
															});
														}
													}
												}),
												xable: function(checked, value) {
													if (checked || value) {
														this.disable();
													}
													else {
														this.enable();
													}
												},
												listeners: {
													added: function() {
														TR.cmp.params.organisationunit.treepanel = this;
													},
													render: function() {
														this.rendered = true;
													},
													itemclick : function(view,rec,item,index,eventObj){
														TR.state.orgunitIds = [];
														var selectedNodes = TR.cmp.params.organisationunit.treepanel.getSelectionModel().getSelection();
														for( var i=0; i<selectedNodes.length; i++ ){
															TR.state.orgunitIds.push( selectedNodes[i].data.id);
														}
													},
													itemcontextmenu: function(v, r, h, i, e) {
														v.getSelectionModel().select(r, false);

														if (v.menu) {
															v.menu.destroy();
														}
														v.menu = Ext.create('Ext.menu.Menu', {
															id: 'treepanel-contextmenu',
															showSeparator: false
														});
														if (!r.data.leaf) {
															v.menu.add({
																id: 'treepanel-contextmenu-item',
																text: TR.i18n.select_all_children,
																icon: 'images/node-select-child.png',
																handler: function() {
																	r.expand(false, function() {
																		v.getSelectionModel().select(r.childNodes, true);
																		v.getSelectionModel().deselect(r);
																		
																		TR.state.orgunitIds = [];
																		for( var i in r.childNodes){
																			 TR.state.orgunitIds.push( r.childNodes[i].data.id );
																		}
																	});
																}
															});
														}
														else {
															return;
														}

														v.menu.showAt(e.xy);
													}
												}
											}
										],
										listeners: {
											added: function() {
												TR.cmp.params.organisationunit.panel = this;
											},
											expand: function() {
												TR.cmp.params.organisationunit.treepanel.setHeight(TR.cmp.params.organisationunit.panel.getHeight() - TR.conf.layout.west_fill_accordion_organisationunit - 60 );
												TR.cmp.params.organisationunit.treepanel.selectRootIf();
											}
										}
									},
									
									// DATA ELEMENTS
									{
										title: '<div id="dataElementTabTitle" style="height:17px;background-image:url(images/data.png); background-repeat:no-repeat; padding-left:20px;">' + TR.i18n.data_items + '</div>',
										hideCollapseTool: true,
										cls: 'tr-accordion-last',
										items: [
											{
												xtype: 'combobox',
												cls: 'tr-combo',
												id: 'sectionCombobox',
												style: 'margin:0 2px 2px;',
												fieldLabel: TR.i18n.section,
												labelStyle: 'padding-left:2px',
												emptyText: TR.i18n.please_select,
												queryMode: 'local',
												editable: false,
												valueField: 'id',
												displayField: 'name',
												width: TR.conf.layout.west_fieldset_width + 8,
												store: TR.store.programStageSection,
												listeners: {
													added: function() {
														TR.cmp.settings.programStageSections = this;
													},
													select: function(cb) {
														var store = TR.store.dataelement.available;
														TR.store.dataelement.selected.loadData([],false);
														store.parent = cb.getValue();
														
														if (TR.util.store.containsParent(store)) {
															TR.util.store.loadFromStorage(store);
															TR.util.multiselect.filterAvailable(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected);
														}
														else {
															if( cb.getValue() == '' ){
																var programStageId = TR.cmp.params.programStage.getValue();
																store.load({params: {programStageId: programStageId}});
															}
															else{
																store.load({params: {sectionId: cb.getValue()}});
															}
														}
													}
												}
											},
											{
												xtype: 'panel',
												layout: 'column',
												bodyStyle: 'border-style:none;height:700px;',
												items: [
													{
														xtype: 'toolbar',
														id: 'avalableDEBar',
														width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2 + 14,
														cls: 'tr-toolbar-multiselect-left',
														style: 'border-bottom:0 none; border-radius: 0',
														items: [
															{
																xtype: 'label',	
																text: TR.i18n.available,
																cls: 'tr-toolbar-multiselect-left-label'
															},
															'->',
															{
																xtype: 'button',
																icon: 'images/arrowright.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.select(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected, 'filterPanel');
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.available, Ext.getCmp('deFilterAvailable').getValue());
																}
															},
															{
																xtype: 'button',
																icon: 'images/arrowrightdouble.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.selectAll(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected, 'filterPanel');
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.available, Ext.getCmp('deFilterAvailable').getValue());
																}
															},
															' '
														]
													},
													{
														xtype: 'toolbar',
														id: 'selectedDEBar',
														width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2 + 14,
														cls: 'tr-toolbar-multiselect-right',
														style: 'border-bottom:0 none; border-radius: 0',
														items: [
															' ',
															{
																xtype: 'button',
																icon: 'images/arrowleftdouble.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.unselectAll(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected, 'filterPanel');
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.selected, Ext.getCmp('deFilterSelected').getValue());
																}
															},
															{
																xtype: 'button',
																icon: 'images/arrowleft.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.unselect(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected, 'filterPanel');
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.selected, Ext.getCmp('deFilterSelected').getValue());
																}
															},
															'->',
															{
																xtype: 'label',
																text: TR.i18n.selected,
																cls: 'tr-toolbar-multiselect-right-label'
															}
														]
													},	
													{
														xtype: 'multiselect',
														id: 'availableDataelements',
														name: 'availableDataelements',
														cls: 'tr-toolbar-multiselect-left',
														width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2 + 14,
														height: TR.conf.layout.west_dataelements_multiselect,
														displayField: 'name',
														valueField: 'id',
														queryMode: 'remote',
														store: TR.store.dataelement.available,
														tbar: [
															{
																xtype: 'textfield',
																emptyText: TR.i18n.filter,
																id: 'deFilterAvailable',
																width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2 - 64,
																listeners: {			
																	specialkey: function( textfield, e, eOpts ){
																		if ( e.keyCode == e.ENTER )
																		{
																			TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.available, textfield.rawValue.toLowerCase());	
																		}
																	}
																}
															},
															{
																xtype: 'button',
																icon: 'images/filter.png',
																tooltip: TR.i18n.filter,
																width: 24,
																handler: function() {
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.available, Ext.getCmp('deFilterAvailable').getValue());
																}
															},
															{
																xtype: 'image',
																src: 'images/grid-split.png',
																width: 1,
																height: 14
															},
															{
																xtype: 'button',
																icon: 'images/clear-filter.png',
																tooltip: TR.i18n.clear,
																width: 24,
																handler: function() {
																	Ext.getCmp('deFilterAvailable').setValue('');
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.available, Ext.getCmp('deFilterAvailable').getValue());
																}
															}
														],
														listeners: {
															added: function() {
																TR.cmp.params.dataelement.available = this;
															},                                                                
															afterrender: function() {
																this.boundList.on('itemdblclick', function() {
																	TR.util.multiselect.select(this, TR.cmp.params.dataelement.selected, 'filterPanel');
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.available, Ext.getCmp('deFilterAvailable').getValue());
																}, this);																
															}
														}
													},											
													{
														xtype: 'multiselect',
														id: 'selectedDataelements',
														name: 'selectedDataelements',
														cls: 'tr-toolbar-multiselect-right',
														width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2 + 14,
														height: TR.conf.layout.west_dataelements_multiselect,
														displayField: 'name',
														valueField: 'id',
														ddReorder: true,
														queryMode: 'remote',
														store: TR.store.dataelement.selected,
														tbar: [
															{
																xtype: 'textfield',
																emptyText: TR.i18n.filter,
																id: 'deFilterSelected',
																width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2 - 64,
																listeners: {			
																	specialkey: function( textfield, e, eOpts ){
																		if ( e.keyCode == e.ENTER )
																		{
																			TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.selected, textfield.rawValue.toLowerCase());	
																		}
																	}
																}
															},
															{
																xtype: 'button',
																icon: 'images/filter.png',
																tooltip: TR.i18n.filter,
																width: 24,
																handler: function() {
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.selected, Ext.getCmp('deFilterSelected').getValue());
																}
															},
															{
																xtype: 'image',
																src: 'images/grid-split.png',
																width: 1,
																height: 14
															},
															{
																xtype: 'button',
																icon: 'images/clear-filter.png',
																tooltip: TR.i18n.clear,
																width: 24,
																handler: function() {
																	Ext.getCmp('deFilterSelected').setValue('');
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.selected, Ext.getCmp('deFilterSelected').getValue());
																}
															}
														],
														listeners: {
															added: function() {
																TR.cmp.params.dataelement.selected = this;
															},
															afterrender: function() {
																this.boundList.on('itemdblclick', function() {
																	TR.util.multiselect.unselect(TR.cmp.params.dataelement.available, this, 'filterPanel');
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.available, Ext.getCmp('deFilterAvailable').getValue());
																}, this);
															}
														}
													},
													{
														xtype: 'toolbar',
														width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) + 28,
														cls: 'tr-toolbar-multiselect-left',
														style: 'margin-top:2px; border-bottom:0 none',
														items: [
															{
																xtype: 'label',	
																text: TR.i18n.selected_items,
																cls: 'tr-toolbar-multiselect-left-label'
															},
															'->',
															{
																xtype: 'button',
																icon: 'images/arrowup.png',
																tooltip: TR.i18n.show_hide_selected_values,
																up: true,
																width: 22,
																handler: function() {
																	if(this.up==true){
																		Ext.getCmp('avalableDEBar').setVisible(false);
																		Ext.getCmp('selectedDEBar').setVisible(false);
																		Ext.getCmp('availableDataelements').setVisible(false);
																		Ext.getCmp('selectedDataelements').setVisible(false);
																		if(Ext.getCmp('reportTypeGroup').getValue().reportType=='true'){
																			Ext.getCmp('filterPanel').setHeight(TR.conf.layout.west_dataelements_expand_filter_panel);
																		}
																		else{
																			Ext.getCmp('filterPanel').setHeight(TR.conf.layout.west_dataelements_expand_aggregate_filter_panel);
																		}
																		this.setIcon('images/arrowdown.png');
																		this.up = false;
																	}
																	else{
																		Ext.getCmp('avalableDEBar').setVisible(true);
																		Ext.getCmp('selectedDEBar').setVisible(true);
																		Ext.getCmp('availableDataelements').setVisible(true);
																		Ext.getCmp('selectedDataelements').setVisible(true);
																		if(Ext.getCmp('reportTypeGroup').getValue().reportType=='true'){
																			Ext.getCmp('filterPanel').setHeight(TR.conf.layout.west_dataelements_collapse_filter_panel);
																		}
																		else{
																			Ext.getCmp('filterPanel').setHeight(TR.conf.layout.west_dataelements_collapse_aggregate_filter_panel);
																		}
																		this.setIcon('images/arrowup.png');
																		this.up = true;
																	}
																}
															}
														]
													},
													{
														xtype: 'panel',
														layout: 'column',
														id: 'filterPanel',
														bodyStyle: 'background-color:transparent; padding:2px 2px 2px 2px;overflow-x:hidden;overflow-y:scroll;',
														overflowX: 'hidden',
														height: TR.conf.layout.west_dataelements_filter_panel,
														width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) + 28,
														items: []
													}
												]
											},
										],
										listeners: {
											added: function() {
												TR.cmp.params.dataelement.panel = this;
											}
										}
									}
								
								]
							}
						]
					}
				],
                listeners: {
                    added: function() {
                        TR.cmp.region.west = this;
                    },
                    collapse: function() {                    
                        this.collapsed = true;
                        TR.cmp.toolbar.resizewest.setText('>>>');
                    },
                    expand: function() {
                        this.collapsed = false;
                        TR.cmp.toolbar.resizewest.setText('<<<');
                    }
                }
            },
			// button for main form
            {
                id: 'center',
                region: 'center',
                layout: 'fit',
                bodyStyle: 'padding-top:0px, padding-bottom:0px;',
                tbar: {
                    xtype: 'toolbar',
                    cls: 'tr-toolbar',
                    height: TR.conf.layout.center_tbar_height,
                    defaults: {
                        height: 26
                    },
                    items: [
					{
						xtype: 'button',
						name: 'resizewest',
						cls: 'tr-toolbar-btn-2',
						text: '<<<',
						tooltip: TR.i18n.show_hide_settings,
						handler: function() {
							var p = TR.cmp.region.west;
							if (p.collapsed) {
								p.expand();
							}
							else {
								p.collapse();
							}
						},
						listeners: {
							added: function() {
								TR.cmp.toolbar.resizewest = this;
							}
						}
					},
					{
						xtype: 'button',
						cls: 'tr-toolbar-btn-1',
						text: TR.i18n.update,
						handler: function() {
							TR.exe.execute();
						}
					},
					{
						xtype: 'button',
						cls: 'tr-toolbar-btn-2',
						id: 'layoutBtn',
						hidden: true,
						text: TR.i18n.table_layout,
						hidden: true,
						handler: function() {
							TR.cmp.layoutWindow.window.show();
						}
					},
					{
						xtype: 'button',
						cls: 'tr-toolbar-btn-2',
						text: TR.i18n.options,
						handler: function() {
							TR.cmp.options.window.show();
						}
					},
					{
						xtype: 'button',
						text: TR.i18n.clear_filter,
						id: 'btnClean',
						disabled: true,
						handler: function() {
							if(Ext.getCmp('reportTypeGroup').getValue().reportType=='true')
							{
								TR.cmp.params.dataelement.selected.store.each( function(r) {
									var deId = r.data.id;
									var length = Ext.getCmp('filterPanel_' + deId).items.length/5;
									for(var idx=0;idx<length;idx++)
									{					
										var id = deId + '_' + idx;
										Ext.getCmp('filter_' + id).setValue('');
									}
								});
							}
							else
							{
								TR.store.dataelement.selected.removeAll();
								Ext.getCmp('filterPanel').removeAll();
								Ext.getCmp('filterPanel').doLayout();
							}
							TR.exe.execute();
						}
					},
					{
						xtype: 'button',
						cls: 'tr-toolbar-btn-2',
						id: 'caseBasedFavoriteBtn',
						text: TR.i18n.favorites,
						menu: {},
						hidden: true,
						handler: function() {
							if (TR.cmp.caseBasedFavorite.window) {
								TR.cmp.caseBasedFavorite.window.destroy();
							}
							TR.cmp.caseBasedFavorite.window = TR.app.CaseFavoriteWindow();
							TR.cmp.caseBasedFavorite.window.show();
						},
						listeners: {
							added: function() {
								TR.cmp.toolbar.favorite = this;
							}
						}
					},
					
					// Aggregate Favorite button
					{
						xtype: 'button',
						cls: 'tr-toolbar-btn-2',
						id: 'aggregateFavoriteBtn',
						text: TR.i18n.favorites,
						menu: {},
						handler: function() {
							if (TR.cmp.aggregateFavorite.window) {
								TR.cmp.aggregateFavorite.window.destroy();
							}
							TR.cmp.aggregateFavorite.window = TR.app.AggregateFavoriteWindow();
							TR.cmp.aggregateFavorite.window.show();
						},
						listeners: {
							added: function() {
								TR.cmp.toolbar.favorite = this;
							}
						}
					},
					{
						xtype: 'button',
						text: TR.i18n.download,
						menu: {},
						execute: function(type) {
							TR.exe.execute( type );
						},
						listeners: {
							afterrender: function(b) {
								this.menu = Ext.create('Ext.menu.Menu', {
									margin: '2 0 0 0',
									shadow: false,
									showSeparator: false,
									items: [
										{
											text: TR.i18n.xls,
											iconCls: 'tr-menu-item-xls',
											minWidth: 105,
											handler: function() {
												b.execute(TR.conf.finals.download.xls);
											}
										},
										{
											text: TR.i18n.csv,
											iconCls: 'tr-menu-item-csv',
											id: 'downloadCvsIcon',
											minWidth: 105,
											handler: function() {
												b.execute(TR.conf.finals.download.csv);
											}
										}										
									]                                            
								});
							}
						}
					},
					'->',
					{
						xtype: 'button',
						cls: 'tr-toolbar-btn-2',
						text: TR.i18n.home,
						handler: function() {
							window.location.href = TR.conf.finals.ajax.path_commons + TR.conf.finals.ajax.redirect;
						}
					},]
                },
                bbar: {
					items: [
						{
							xtype: 'panel',
							cls: 'tr-statusbar',
							height: 24,
							listeners: {
								added: function() {
									TR.cmp.statusbar.panel = this;
								}
							}
						}
					]
				},					
                listeners: {
                    added: function() {
                        TR.cmp.region.center = this;
                    },
                    resize: function() {
						if (TR.cmp.statusbar.panel) {
							TR.cmp.statusbar.panel.setWidth(TR.cmp.region.center.getWidth());
						}
					}
                }
            },
            {
                region: 'east',
                preventHeader: true,
                collapsible: true,
                collapsed: true,
                collapseMode: 'mini',
                listeners: {
                    afterrender: function() {
                        TR.cmp.region.east = this;
                    }
                }
            }
        ],
        listeners: {
            afterrender: function(vp) {
                TR.init.initialize(vp);
				TR.cmp.options.window = TR.app.OptionsWindow();
				TR.cmp.options.window.hide();
				
				TR.cmp.layoutWindow.window = TR.app.LayoutWindow();
				TR.cmp.layoutWindow.window.hide();
				
				Ext.getCmp('reportTypeGroup').setValue(true);
				Ext.getCmp('limitOption').setVisible(false);
				Ext.getCmp('deSumCbx').setVisible(false);
				Ext.getCmp('aggregateType').setVisible(false);
				Ext.getCmp('downloadCvsIcon').setVisible(false);
				Ext.getCmp('aggregateFavoriteBtn').setVisible(false);
				Ext.getCmp('caseBasedFavoriteBtn').setVisible(true);
				Ext.getCmp('relativePeriodsDiv').setVisible(false); 
				
				TR.state.orgunitIds = [];
				for( var i in TR.init.system.rootnodes){
					TR.state.orgunitIds.push( TR.init.system.rootnodes[i].id );
				}
				
				if( TR.init.report.id != "")
				{
					Ext.getCmp('reportTypeGroup').setValue(true);
					TR.util.crud.favorite.run(TR.init.report.id);
				}
            },
            resize: function(vp) {
                TR.cmp.region.west.setWidth(TR.conf.layout.west_width);
                
				TR.util.viewport.resizeParams();
                
                if (TR.datatable.datatable) {
                    TR.datatable.datatable.setHeight( TR.util.viewport.getSize().y - 68 );
                }
            }
        }
    });
    
	
    }});
}); 
