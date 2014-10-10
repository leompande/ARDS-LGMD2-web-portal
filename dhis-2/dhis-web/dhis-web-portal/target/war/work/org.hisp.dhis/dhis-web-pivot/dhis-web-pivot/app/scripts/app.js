Ext.onReady( function() {
	var NS = PT,

		LayoutWindow,
		OptionsWindow,
		FavoriteWindow,
		SharingWindow,
		InterpretationWindow,

		extendCore,
		createViewport,
		dimConf,

		ns = {
			core: {},
			app: {}
		};

	// set app config
	(function() {

		// ext configuration
		Ext.QuickTips.init();

		Ext.override(Ext.LoadMask, {
			onHide: function() {
				this.callParent();
			}
		});

		// right click handler
		document.body.oncontextmenu = function() {
			return false;
		};
	}());

	// constructors
	LayoutWindow = function() {
		var dimension,
			dimensionStore,
			row,
			rowStore,
			col,
			colStore,
			filter,
			filterStore,
			value,

			getData,
			getStore,
			getStoreKeys,
			getCmpHeight,
			getSetup,

			dimensionPanel,
			selectPanel,
			window,

			margin = 2,
			defaultWidth = 160,
			defaultHeight = 158,
			maxHeight = (ns.app.viewport.getHeight() - 100) / 2;

		getData = function(all) {
			var data = [];

			if (all) {
				data.push({id: dimConf.data.dimensionName, name: dimConf.data.name});
			}

			data.push({id: dimConf.category.dimensionName, name: dimConf.category.name});

			if (all) {
				data.push({id: dimConf.period.dimensionName, name: dimConf.period.name});
				data.push({id: dimConf.organisationUnit.dimensionName, name: dimConf.organisationUnit.name});
			}

			return data.concat(Ext.clone(ns.core.init.dimensions));
		};

		getStore = function(data) {
			var config = {};

			config.fields = ['id', 'name'];

			if (data) {
				config.data = data;
			}

			config.getDimensionNames = function() {
				var dimensionNames = [];

				this.each(function(r) {
					dimensionNames.push(r.data.id);
				});

				return Ext.clone(dimensionNames);
			};

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

		dimensionStore = getStore(getData());
		dimensionStore.reset = function(all) {
			dimensionStore.removeAll();
			dimensionStore.add(getData(all));
		};
		ns.app.stores.dimension = dimensionStore;

		rowStore = getStore();
		ns.app.stores.row = rowStore;
		rowStore.add({id: dimConf.period.dimensionName, name: dimConf.period.name});

		colStore = getStore();
		ns.app.stores.col = colStore;
		colStore.add({id: dimConf.data.dimensionName, name: dimConf.data.name});

		filterStore = getStore();
		ns.app.stores.filter = filterStore;
		filterStore.add({id: dimConf.organisationUnit.dimensionName, name: dimConf.organisationUnit.name});

		getCmpHeight = function() {
			var size = dimensionStore.totalCount,
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

		dimension = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-leftright',
			width: defaultWidth,
			height: (getCmpHeight() * 2) + margin,
			style: 'margin-right:' + margin + 'px; margin-bottom:0px',
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'layoutDD',
			dropGroup: 'layoutDD',
			ddReorder: false,
			store: dimensionStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: NS.i18n.dimensions,
					cls: 'ns-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		row = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-leftright',
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
					text: NS.i18n.row,
					cls: 'ns-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.boundList.on('itemdblclick', function(view, record) {
						ms.store.remove(record);
						dimensionStore.add(record);
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
			cls: 'ns-toolbar-multiselect-leftright',
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
					text: NS.i18n.column,
					cls: 'ns-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.boundList.on('itemdblclick', function(view, record) {
						ms.store.remove(record);
						dimensionStore.add(record);
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
			cls: 'ns-toolbar-multiselect-leftright',
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
					text: NS.i18n.filter,
					cls: 'ns-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.boundList.on('itemdblclick', function(view, record) {
						ms.store.remove(record);
						dimensionStore.add(record);
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
			title: NS.i18n.table_layout,
			bodyStyle: 'background-color:#fff; padding:2px',
			closeAction: 'hide',
			autoShow: true,
			modal: true,
			resizable: false,
			getSetup: getSetup,
			dimensionStore: dimensionStore,
			rowStore: rowStore,
			colStore: colStore,
			filterStore: filterStore,
			hideOnBlur: true,
			items: {
				layout: 'column',
				bodyStyle: 'border:0 none',
				items: [
					dimension,
					selectPanel
				]
			},
			bbar: [
				'->',
				{
					text: NS.i18n.hide,
					listeners: {
						added: function(b) {
							b.on('click', function() {
								window.hide();
							});
						}
					}
				},
				{
					text: '<b>' + NS.i18n.update + '</b>',
					listeners: {
						added: function(b) {
							b.on('click', function() {
								var config = ns.core.web.pivot.getLayoutConfig(),
									layout = ns.core.api.layout.Layout(config);

								if (!layout) {
									return;
								}

								ns.core.web.pivot.getData(layout, false);

								window.hide();
							});
						}
					}
				}
			],
			listeners: {
				show: function(w) {
					if (ns.app.layoutButton.rendered) {
						ns.core.web.window.setAnchorPosition(w, ns.app.layoutButton);

						if (!w.hasHideOnBlurHandler) {
							ns.core.web.window.addHideOnBlurHandler(w);
						}
					}
				}
			}
		});

		return window;
	};

	OptionsWindow = function() {
		var showTotals,
			showSubTotals,
			hideEmptyRows,
            aggregationType,
			showHierarchy,
			digitGroupSeparator,
			displayDensity,
			fontSize,
			reportingPeriod,
			organisationUnit,
			parentOrganisationUnit,

			data,
			style,
			parameters,

			comboboxWidth = 262,
			window;

		showTotals = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.show_totals,
			style: 'margin-bottom:4px',
			checked: true
		});

		showSubTotals = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.show_subtotals,
			style: 'margin-bottom:4px',
			checked: true
		});

		hideEmptyRows = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.hide_empty_rows,
			style: 'margin-bottom:4px'
		});

		aggregationType = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			style: 'margin-bottom:3px',
			width: comboboxWidth,
			labelWidth: 130,
			fieldLabel: NS.i18n.aggregation_type,
			labelStyle: 'color:#333',
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 'default',
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 'default', text: NS.i18n.by_data_element},
					{id: 'count', text: NS.i18n.count},
					{id: 'sum', text: NS.i18n.sum}
				]
			})
		});
        
		showHierarchy = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.show_hierarchy,
			style: 'margin-bottom:4px'
		});

		displayDensity = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			style: 'margin-bottom:3px',
			width: comboboxWidth,
			labelWidth: 130,
			fieldLabel: NS.i18n.display_density,
			labelStyle: 'color:#333',
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 'normal',
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 'comfortable', text: NS.i18n.comfortable},
					{id: 'normal', text: NS.i18n.normal},
					{id: 'compact', text: NS.i18n.compact}
				]
			})
		});

		fontSize = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			style: 'margin-bottom:3px',
			width: comboboxWidth,
			labelWidth: 130,
			fieldLabel: NS.i18n.font_size,
			labelStyle: 'color:#333',
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 'normal',
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 'large', text: NS.i18n.large},
					{id: 'normal', text: NS.i18n.normal},
					{id: 'small', text: NS.i18n.small_}
				]
			})
		});

		digitGroupSeparator = Ext.create('Ext.form.field.ComboBox', {
			labelStyle: 'color:#333',
			cls: 'ns-combo',
			style: 'margin-bottom:3px',
			width: comboboxWidth,
			labelWidth: 130,
			fieldLabel: NS.i18n.digit_group_separator,
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 'space',
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 'comma', text: 'Comma'},
					{id: 'space', text: 'Space'},
					{id: 'none', text: 'None'}
				]
			})
		});

		legendSet = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			style: 'margin-bottom:3px',
			width: comboboxWidth,
			labelWidth: 130,
			fieldLabel: NS.i18n.legend_set,
			valueField: 'id',
			displayField: 'name',
			editable: false,
			value: 0,
			store: ns.app.stores.legendSet
		});

		reportingPeriod = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.reporting_period,
			style: 'margin-bottom:4px',
		});

		organisationUnit = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.organisation_unit,
			style: 'margin-bottom:4px',
		});

		parentOrganisationUnit = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.parent_organisation_unit,
			style: 'margin-bottom:4px',
		});

		regression = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.include_regression,
			style: 'margin-bottom:4px',
		});

		cumulative = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: NS.i18n.include_cumulative,
			style: 'margin-bottom:6px',
		});

		sortOrder = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			style: 'margin-bottom:3px',
			width: 250,
			labelWidth: 130,
			fieldLabel: NS.i18n.sort_order,
			labelStyle: 'color:#333',
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 0,
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 0, text: NS.i18n.none},
					{id: 1, text: NS.i18n.low_to_high},
					{id: 2, text: NS.i18n.high_to_low}
				]
			})
		});

		topLimit = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			style: 'margin-bottom:0',
			width: 250,
			labelWidth: 130,
			fieldLabel: NS.i18n.top_limit,
			labelStyle: 'color:#333',
			queryMode: 'local',
			valueField: 'id',
			editable: false,
			value: 0,
			store: Ext.create('Ext.data.Store', {
				fields: ['id', 'text'],
				data: [
					{id: 0, text: NS.i18n.none},
					{id: 5, text: 5},
					{id: 10, text: 10},
					{id: 20, text: 20},
					{id: 50, text: 50},
					{id: 100, text: 100}
				]
			})
		});

		data = {
			bodyStyle: 'border:0 none',
			style: 'margin-left:14px',
			items: [
				showTotals,
				showSubTotals,
				hideEmptyRows,
                aggregationType
			]
		};

		organisationUnits = {
			bodyStyle: 'border:0 none',
			style: 'margin-left:14px',
			items: [
				showHierarchy
			]
		};

		style = {
			bodyStyle: 'border:0 none',
			style: 'margin-left:14px',
			items: [
				displayDensity,
				fontSize,
				digitGroupSeparator,
				legendSet
			]
		};

		parameters = {
			bodyStyle: 'border:0 none; background:transparent',
			style: 'margin-left:14px',
			items: [
				reportingPeriod,
				organisationUnit,
				parentOrganisationUnit,
				regression,
				cumulative,
				sortOrder,
				topLimit
			]
		};

		window = Ext.create('Ext.window.Window', {
			title: NS.i18n.table_options,
			bodyStyle: 'background-color:#fff; padding:5px',
			closeAction: 'hide',
			autoShow: true,
			modal: true,
			resizable: false,
			hideOnBlur: true,
			getOptions: function() {
				return {
					showTotals: showTotals.getValue(),
					showSubTotals: showSubTotals.getValue(),
					hideEmptyRows: hideEmptyRows.getValue(),
                    aggregationType: aggregationType.getValue(),
					showHierarchy: showHierarchy.getValue(),
					displayDensity: displayDensity.getValue(),
					fontSize: fontSize.getValue(),
					digitGroupSeparator: digitGroupSeparator.getValue(),
					legendSet: {id: legendSet.getValue()},
					reportingPeriod: reportingPeriod.getValue(),
					organisationUnit: organisationUnit.getValue(),
					parentOrganisationUnit: parentOrganisationUnit.getValue(),
					regression: regression.getValue(),
					cumulative: cumulative.getValue(),
					sortOrder: sortOrder.getValue(),
					topLimit: topLimit.getValue()
				};
			},
			setOptions: function(layout) {
				showTotals.setValue(Ext.isBoolean(layout.showTotals) ? layout.showTotals : true);
				showSubTotals.setValue(Ext.isBoolean(layout.showSubTotals) ? layout.showSubTotals : true);
				hideEmptyRows.setValue(Ext.isBoolean(layout.hideEmptyRows) ? layout.hideEmptyRows : false);
                aggregationType.setValue(Ext.isString(layout.aggregationType) ? layout.aggregationType : 'default');
				showHierarchy.setValue(Ext.isBoolean(layout.showHierarchy) ? layout.showHierarchy : false);
				displayDensity.setValue(Ext.isString(layout.displayDensity) ? layout.displayDensity : 'normal');
				fontSize.setValue(Ext.isString(layout.fontSize) ? layout.fontSize : 'normal');
				digitGroupSeparator.setValue(Ext.isString(layout.digitGroupSeparator) ? layout.digitGroupSeparator : 'space');
				legendSet.setValue(Ext.isObject(layout.legendSet) && Ext.isString(layout.legendSet.id) ? layout.legendSet.id : 0);
				reportingPeriod.setValue(Ext.isBoolean(layout.reportingPeriod) ? layout.reportingPeriod : false);
				organisationUnit.setValue(Ext.isBoolean(layout.organisationUnit) ? layout.organisationUnit : false);
				parentOrganisationUnit.setValue(Ext.isBoolean(layout.parentOrganisationUnit) ? layout.parentOrganisationUnit : false);
				regression.setValue(Ext.isBoolean(layout.regression) ? layout.regression : false);
				cumulative.setValue(Ext.isBoolean(layout.cumulative) ? layout.cumulative : false);
				sortOrder.setValue(Ext.isNumber(layout.sortOrder) ? layout.sortOrder : 0);
				topLimit.setValue(Ext.isNumber(layout.topLimit) ? layout.topLimit : 0);
			},
			items: [
				{
					bodyStyle: 'border:0 none; color:#222; font-size:12px; font-weight:bold',
					style: 'margin-bottom:6px; margin-left:2px',
					html: NS.i18n.data
				},
				data,
				{
					bodyStyle: 'border:0 none; padding:7px'
				},
				{
					bodyStyle: 'border:0 none; color:#222; font-size:12px; font-weight:bold',
					style: 'margin-bottom:6px; margin-left:2px',
					html: NS.i18n.organisation_units
				},
				organisationUnits,
				{
					bodyStyle: 'border:0 none; padding:7px'
				},
				{
					bodyStyle: 'border:0 none; color:#222; font-size:12px; font-weight:bold',
					style: 'margin-bottom:6px; margin-left:2px',
					html: NS.i18n.style
				},
				style,
				{
					bodyStyle: 'border:0 none; padding:4px'
				},
				{
					bodyStyle: 'border:1px solid #d5d5d5; padding:5px; background-color:#f0f0f0',
					items: [
						{
							bodyStyle: 'border:0 none; padding:0 5px 6px 2px; background-color:transparent; color:#222; font-size:12px',
							html: '<b>' + NS.i18n.parameters + '</b> <span style="font-size:11px"> (' + NS.i18n.for_standard_reports_only + ')</span>'
						},
						parameters
					]
				}
			],
			bbar: [
				'->',
				{
					text: NS.i18n.hide,
					handler: function() {
						window.hide();
					}
				},
				{
					text: '<b>' + NS.i18n.update + '</b>',
					handler: function() {
						var config = ns.core.web.pivot.getLayoutConfig(),
							layout = ns.core.api.layout.Layout(config);

						if (!layout) {
							return;
						}

						ns.core.web.pivot.getData(layout, false);

						window.hide();
					}
				}
			],
			listeners: {
				show: function(w) {
					if (ns.app.optionsButton.rendered) {
						ns.core.web.window.setAnchorPosition(w, ns.app.optionsButton);

						if (!w.hasHideOnBlurHandler) {
							ns.core.web.window.addHideOnBlurHandler(w);
						}
					}

					if (!legendSet.store.isLoaded) {
						legendSet.store.load();
					}

					// cmp
					w.showTotals = showTotals;
					w.showSubTotals = showSubTotals;
					w.hideEmptyRows = hideEmptyRows;
                    w.aggregationType = aggregationType;
					w.showHierarchy = showHierarchy;
					w.displayDensity = displayDensity;
					w.fontSize = fontSize;
					w.digitGroupSeparator = digitGroupSeparator;
					w.legendSet = legendSet;
					w.reportingPeriod = reportingPeriod;
					w.organisationUnit = organisationUnit;
					w.parentOrganisationUnit = parentOrganisationUnit;
					w.regression = regression;
					w.cumulative = cumulative;
					w.sortOrder = sortOrder;
					w.topLimit = topLimit;
				}
			}
		});

		return window;
	};

	FavoriteWindow = function() {

		// Objects
		var NameWindow,

		// Instances
			nameWindow,

		// Functions
			getBody,

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

		ns.app.stores.reportTable.on('load', function(store, records) {
			var pager = store.proxy.reader.jsonData.pager;

			info.setText('Page ' + pager.page + ' of ' + pager.pageCount);

			prevButton.enable();
			nextButton.enable();

			if (pager.page === 1) {
				prevButton.disable();
			}

			if (pager.page === pager.pageCount) {
				nextButton.disable();
			}
		});

		getBody = function() {
			var favorite,
				dimensions;

			if (ns.app.layout) {
				favorite = Ext.clone(ns.app.layout);
				dimensions = [].concat(favorite.columns || [], favorite.rows || [], favorite.filters || []);

				// Server sync
				favorite.totals = favorite.showTotals;
				delete favorite.showTotals;

				favorite.subtotals = favorite.showSubTotals;
				delete favorite.showSubTotals;

				favorite.reportParams = {
					paramReportingPeriod: favorite.reportingPeriod,
					paramOrganisationUnit: favorite.organisationUnit,
					paramParentOrganisationUnit: favorite.parentOrganisationUnit
				};
				delete favorite.reportingPeriod;
				delete favorite.organisationUnit;
				delete favorite.parentOrganisationUnit;

				delete favorite.parentGraphMap;

                delete favorite.id;

				// Replace operand id characters
				for (var i = 0; i < dimensions.length; i++) {
					if (dimensions[i].dimension === ns.core.conf.finals.dimension.operand.objectName) {
						for (var j = 0; j < dimensions[i].items.length; j++) {
							dimensions[i].items[j].id = dimensions[i].items[j].id.replace('-', '.');
						}
					}
				}
			}

			return favorite;
		};

		NameWindow = function(id) {
			var window,
				record = ns.app.stores.reportTable.getById(id);

			nameTextfield = Ext.create('Ext.form.field.Text', {
				height: 26,
				width: 371,
				fieldStyle: 'padding-left: 5px; border-radius: 1px; border-color: #bbb; font-size:11px',
				style: 'margin-bottom:0',
				emptyText: 'Favorite name',
				value: id ? record.data.name : '',
				listeners: {
					afterrender: function() {
						this.focus();
					}
				}
			});

			createButton = Ext.create('Ext.button.Button', {
				text: NS.i18n.create,
				handler: function() {
					var favorite = getBody();
					favorite.name = nameTextfield.getValue();

					//tmp
					//delete favorite.legendSet;

					if (favorite && favorite.name) {
						Ext.Ajax.request({
							url: ns.core.init.contextPath + '/api/reportTables/',
							method: 'POST',
							headers: {'Content-Type': 'application/json'},
							params: Ext.encode(favorite),
							failure: function(r) {
								ns.core.web.mask.show();
								alert(r.responseText);
							},
							success: function(r) {
								var id = r.getAllResponseHeaders().location.split('/').pop();

								ns.app.layout.id = id;
								ns.app.xLayout.id = id;

								ns.app.layout.name = name;
								ns.app.xLayout.name = name;

								ns.app.stores.reportTable.loadStore();

								ns.app.shareButton.enable();

								window.destroy();
							}
						});
					}
				}
			});

			updateButton = Ext.create('Ext.button.Button', {
				text: NS.i18n.update,
				handler: function() {
					var name = nameTextfield.getValue(),
						reportTable;

					if (id && name) {
						Ext.Ajax.request({
							url: ns.core.init.contextPath + '/api/reportTables/' + id + '.json?viewClass=dimensional&links=false',
							method: 'GET',
							failure: function(r) {
								ns.core.web.mask.show();
								alert(r.responseText);
							},
							success: function(r) {
								reportTable = Ext.decode(r.responseText);
								reportTable.name = name;

								Ext.Ajax.request({
									url: ns.core.init.contextPath + '/api/reportTables/' + reportTable.id,
									method: 'PUT',
									headers: {'Content-Type': 'application/json'},
									params: Ext.encode(reportTable),
									failure: function(r) {
										ns.core.web.mask.show();
										alert(r.responseText);
									},
									success: function(r) {
										if (ns.app.layout && ns.app.layout.id && ns.app.layout.id === id) {
											ns.app.layout.name = name;
											ns.app.xLayout.name = name;
										}

										ns.app.stores.reportTable.loadStore();
										window.destroy();
									}
								});
							}
						});
					}
				}
			});

			cancelButton = Ext.create('Ext.button.Button', {
				text: NS.i18n.cancel,
				handler: function() {
					window.destroy();
				}
			});

			window = Ext.create('Ext.window.Window', {
				title: id ? 'Rename favorite' : 'Create new favorite',
				//iconCls: 'ns-window-title-icon-favorite',
				bodyStyle: 'padding:2px; background:#fff',
				resizable: false,
				modal: true,
				items: nameTextfield,
				destroyOnBlur: true,
				bbar: [
					cancelButton,
					'->',
					id ? updateButton : createButton
				],
				listeners: {
					show: function(w) {
						ns.core.web.window.setAnchorPosition(w, addButton);

						if (!w.hasDestroyBlurHandler) {
							ns.core.web.window.addDestroyOnBlurHandler(w);
						}

						ns.app.favoriteWindow.destroyOnBlur = false;

						nameTextfield.focus(false, 500);
					},
					destroy: function() {
						ns.app.favoriteWindow.destroyOnBlur = true;
					}
				}
			});

			return window;
		};

		addButton = Ext.create('Ext.button.Button', {
			text: NS.i18n.add_new,
			width: 67,
			height: 26,
			style: 'border-radius: 1px;',
			menu: {},
			disabled: !Ext.isObject(ns.app.xLayout),
			handler: function() {
				nameWindow = new NameWindow(null, 'create');
				nameWindow.show();
			}
		});

		searchTextfield = Ext.create('Ext.form.field.Text', {
			width: windowCmpWidth - addButton.width - 11,
			height: 26,
			fieldStyle: 'padding-right: 0; padding-left: 5px; border-radius: 1px; border-color: #bbb; font-size:11px',
			emptyText: NS.i18n.search_for_favorites,
			enableKeyEvents: true,
			currentValue: '',
			listeners: {
				keyup: {
					fn: function() {
						if (this.getValue() !== this.currentValue) {
							this.currentValue = this.getValue();

							var value = this.getValue(),
								url = value ? ns.core.init.contextPath + '/api/reportTables/query/' + value + '.json?viewClass=sharing&links=false' : null,
								store = ns.app.stores.reportTable;

							store.page = 1;
							store.loadStore(url);
						}
					},
					buffer: 100
				}
			}
		});

		prevButton = Ext.create('Ext.button.Button', {
			text: NS.i18n.prev,
			handler: function() {
				var value = searchTextfield.getValue(),
					url = value ? ns.core.init.contextPath + '/api/reportTables/query/' + value + '.json?viewClass=sharing&links=false' : null,
					store = ns.app.stores.reportTable;

				store.page = store.page <= 1 ? 1 : store.page - 1;
				store.loadStore(url);
			}
		});

		nextButton = Ext.create('Ext.button.Button', {
			text: NS.i18n.next,
			handler: function() {
				var value = searchTextfield.getValue(),
					url = value ? ns.core.init.contextPath + '/api/reportTables/query/' + value + '.json?viewClass=sharing&links=false' : null,
					store = ns.app.stores.reportTable;

				store.page = store.page + 1;
				store.loadStore(url);
			}
		});

		info = Ext.create('Ext.form.Label', {
			cls: 'ns-label-info',
			width: 300,
			height: 22
		});

		grid = Ext.create('Ext.grid.Panel', {
			cls: 'ns-grid',
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
									ns.core.web.pivot.loadTable(record.data.id);
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
							iconCls: 'ns-grid-row-icon-edit',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-edit' + (!record.data.access.update ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex);

								if (record.data.access.update) {
									nameWindow = new NameWindow(record.data.id);
									nameWindow.show();
								}
							}
						},
						{
							iconCls: 'ns-grid-row-icon-overwrite',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-overwrite' + (!record.data.access.update ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex),
									message,
									favorite;

								if (record.data.access.update) {
									message = NS.i18n.overwrite_favorite + '?\n\n' + record.data.name;
									favorite = getBody();

									if (favorite) {
										favorite.name = record.data.name;

										if (confirm(message)) {
											Ext.Ajax.request({
												url: ns.core.init.contextPath + '/api/reportTables/' + record.data.id,
												method: 'PUT',
												headers: {'Content-Type': 'application/json'},
												params: Ext.encode(favorite),
												success: function(r) {
													ns.app.layout.id = record.data.id;
													ns.app.xLayout.id = record.data.id;

													ns.app.layout.name = true;
													ns.app.xLayout.name = true;

													ns.app.stores.reportTable.loadStore();

													ns.app.shareButton.enable();
												}
											});
										}
									}
									else {
										alert(NS.i18n.please_create_a_table_first);
									}
								}
							}
						},
						{
							iconCls: 'ns-grid-row-icon-sharing',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-sharing' + (!record.data.access.manage ? ' disabled' : '');
							},
							handler: function(grid, rowIndex) {
								var record = this.up('grid').store.getAt(rowIndex);

								if (record.data.access.manage) {
									Ext.Ajax.request({
										url: ns.core.init.contextPath + '/api/sharing?type=reportTable&id=' + record.data.id,
										method: 'GET',
										failure: function(r) {
											ns.app.viewport.mask.hide();
											alert(r.responseText);
										},
										success: function(r) {
											var sharing = Ext.decode(r.responseText),
												window = SharingWindow(sharing);
											window.show();
										}
									});
								}
							}
						},
						{
							iconCls: 'ns-grid-row-icon-delete',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-delete' + (!record.data.access['delete'] ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex),
									message;

								if (record.data.access['delete']) {
									message = NS.i18n.delete_favorite + '?\n\n' + record.data.name;

									if (confirm(message)) {
										Ext.Ajax.request({
											url: ns.core.init.contextPath + '/api/reportTables/' + record.data.id,
											method: 'DELETE',
											success: function() {
												ns.app.stores.reportTable.loadStore();
											}
										});
									}
								}
							}
						}
					]
				},
				{
					sortable: false,
					width: 6
				}
			],
			store: ns.app.stores.reportTable,
			bbar: [
				info,
				'->',
				prevButton,
				nextButton
			],
			listeners: {
				render: function() {
					var size = Math.floor((ns.app.centerRegion.getHeight() - 155) / ns.core.conf.layout.grid_row_height);
					this.store.pageSize = size;
					this.store.page = 1;
					this.store.loadStore();

					ns.app.stores.reportTable.on('load', function() {
						if (this.isVisible()) {
							this.fireEvent('afterrender');
						}
					}, this);
				},
				afterrender: function() {
					var fn = function() {
						var editArray = Ext.query('.tooltip-favorite-edit'),
							overwriteArray = Ext.query('.tooltip-favorite-overwrite'),
							//dashboardArray = Ext.query('.tooltip-favorite-dashboard'),
							sharingArray = Ext.query('.tooltip-favorite-sharing'),
							deleteArray = Ext.query('.tooltip-favorite-delete'),
							el;

						for (var i = 0; i < editArray.length; i++) {
							var el = editArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: NS.i18n.rename,
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}

						for (var i = 0; i < overwriteArray.length; i++) {
							el = overwriteArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: NS.i18n.overwrite,
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}

						for (var i = 0; i < sharingArray.length; i++) {
							el = sharingArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: NS.i18n.share_with_other_people,
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}

						for (var i = 0; i < deleteArray.length; i++) {
							el = deleteArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: NS.i18n.delete_,
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
			title: NS.i18n.manage_favorites,
			//iconCls: 'ns-window-title-icon-favorite',
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
					ns.core.web.window.setAnchorPosition(w, ns.app.favoriteButton);

					if (!w.hasDestroyOnBlurHandler) {
						ns.core.web.window.addDestroyOnBlurHandler(w);
					}

					searchTextfield.focus(false, 500);
				}
			}
		});

		return favoriteWindow;
	};

	SharingWindow = function(sharing) {

		// Objects
		var UserGroupRow,

		// Functions
			getBody,

		// Components
			userGroupStore,
			userGroupField,
			userGroupButton,
			userGroupRowContainer,
			externalAccess,
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
					{id: 'r-------', name: NS.i18n.can_view},
					{id: 'rw------', name: NS.i18n.can_edit_and_view}
				];

				if (isPublicAccess) {
					data.unshift({id: '-------', name: NS.i18n.none});
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
					fieldLabel: isPublicAccess ? NS.i18n.public_access : obj.name,
					labelStyle: 'color:#333',
					cls: 'ns-combo',
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
					externalAccess: externalAccess ? externalAccess.getValue() : false,
					user: {
						id: ns.core.init.user.id,
						name: ns.core.init.user.name
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
				url: ns.core.init.contextPath + '/api/sharing/search',
				reader: {
					type: 'json',
					root: 'userGroups'
				}
			}
		});

		userGroupField = Ext.create('Ext.form.field.ComboBox', {
			valueField: 'id',
			displayField: 'name',
			emptyText: NS.i18n.search_for_user_groups,
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

		if (sharing.meta.allowExternalAccess) {
			externalAccess = userGroupRowContainer.add({
				xtype: 'checkbox',
				fieldLabel: NS.i18n.allow_external_access,
				labelSeparator: '',
				labelWidth: 250,
				checked: !!sharing.object.externalAccess
			});
		}

		publicGroup = userGroupRowContainer.add(UserGroupRow({
			id: sharing.object.id,
			name: sharing.object.name,
			access: sharing.object.publicAccess
		}, true, !sharing.meta.allowPublicAccess));

		if (Ext.isArray(sharing.object.userGroupAccesses)) {
			for (var i = 0, userGroupRow; i < sharing.object.userGroupAccesses.length; i++) {
				userGroupRow = UserGroupRow(sharing.object.userGroupAccesses[i]);
				userGroupRowContainer.add(userGroupRow);
			}
		}

		window = Ext.create('Ext.window.Window', {
			title: NS.i18n.sharing_settings,
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
					text: NS.i18n.save,
					handler: function() {
						Ext.Ajax.request({
							url: ns.core.init.contextPath + '/api/sharing?type=reportTable&id=' + sharing.object.id,
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
					var pos = ns.app.favoriteWindow.getPosition();
					w.setPosition(pos[0] + 5, pos[1] + 5);

					if (!w.hasDestroyOnBlurHandler) {
						ns.core.web.window.addDestroyOnBlurHandler(w);
					}

					ns.app.favoriteWindow.destroyOnBlur = false;
				},
				destroy: function() {
					ns.app.favoriteWindow.destroyOnBlur = true;
				}
			}
		});

		return window;
	};

	InterpretationWindow = function() {
		var textArea,
			linkPanel,
			shareButton,
			window;

		if (Ext.isString(ns.app.layout.id)) {
			textArea = Ext.create('Ext.form.field.TextArea', {
				cls: 'ns-textarea',
				height: 130,
				fieldStyle: 'padding-left: 4px; padding-top: 3px',
				emptyText: NS.i18n.write_your_interpretation,
				enableKeyEvents: true,
				listeners: {
					keyup: function() {
						shareButton.xable();
					}
				}
			});

			linkPanel = Ext.create('Ext.panel.Panel', {
				html: function() {
					var reportTableUrl = ns.core.init.contextPath + '/dhis-web-pivot/app/index.html?id=' + ns.app.layout.id,
						apiUrl = ns.core.init.contextPath + '/api/reportTables/' + ns.app.layout.id + '/data.html',
						html = '';

					html += '<div><b>Pivot link: </b><span class="user-select"><a href="' + reportTableUrl + '" target="_blank">' + reportTableUrl + '</a></span></div>';
					html += '<div style="padding-top:3px"><b>API link: </b><span class="user-select"><a href="' + apiUrl + '" target="_blank">' + apiUrl + '</a></span></div>';
					return html;
				}(),
				style: 'padding-top: 8px; padding-bottom: 5px',
				bodyStyle: 'border: 0 none'
			});

			shareButton = Ext.create('Ext.button.Button', {
				text: NS.i18n.share,
				disabled: true,
				xable: function() {
					this.setDisabled(!textArea.getValue());
				},
				handler: function() {
					if (textArea.getValue()) {
						Ext.Ajax.request({
							url: ns.core.init.contextPath + '/api/interpretations/reportTable/' + ns.app.layout.id,
							method: 'POST',
							params: textArea.getValue(),
							headers: {'Content-Type': 'text/html'},
							success: function() {
								textArea.reset();
								window.hide();
							}
						});
					}
				}
			});

			window = Ext.create('Ext.window.Window', {
				title: ns.app.layout.name,
				layout: 'fit',
				//iconCls: 'ns-window-title-interpretation',
				width: 500,
				bodyStyle: 'padding:5px 5px 3px; background-color:#fff',
				resizable: false,
				destroyOnBlur: true,
				modal: true,
				items: [
					textArea,
					linkPanel
				],
				bbar: {
					cls: 'ns-toolbar-bbar',
					defaults: {
						height: 24
					},
					items: [
						'->',
						shareButton
					]
				},
				listeners: {
					show: function(w) {
						ns.core.web.window.setAnchorPosition(w, ns.app.shareButton);

						document.body.oncontextmenu = true;

						if (!w.hasDestroyOnBlurHandler) {
							ns.core.web.window.addDestroyOnBlurHandler(w);
						}
					},
					hide: function() {
						document.body.oncontextmenu = function(){return false;};
					},
					destroy: function() {
						ns.app.interpretationWindow = null;
					}
				}
			});

			return window;
		}

		return;
	};

	// core
	extendCore = function(core) {
        var conf = core.conf,
			api = core.api,
			support = core.support,
			service = core.service,
			web = core.web,
			init = core.init;

        // init
        (function() {

			// root nodes
			for (var i = 0; i < init.rootNodes.length; i++) {
				init.rootNodes[i].expanded = true;
				init.rootNodes[i].path = '/' + conf.finals.root.id + '/' + init.rootNodes[i].id;
			}

			// sort organisation unit levels
			if (Ext.isArray(init.organisationUnitLevels)) {
				support.prototype.array.sort(init.organisationUnitLevels, 'ASC', 'level');
			}
		}());

		// web
		(function() {

			// multiSelect
			web.multiSelect = web.multiSelect || {};

			web.multiSelect.select = function(a, s) {
				var selected = a.getValue();
				if (selected.length) {
					var array = [];
					Ext.Array.each(selected, function(item) {
						array.push({id: item, name: a.store.getAt(a.store.findExact('id', item)).data.name});
					});
					s.store.add(array);
				}
				this.filterAvailable(a, s);
			};

			web.multiSelect.selectAll = function(a, s, isReverse) {
				var array = [];
				a.store.each( function(r) {
					array.push({id: r.data.id, name: r.data.name});
				});
				if (isReverse) {
					array.reverse();
				}
				s.store.add(array);
				this.filterAvailable(a, s);
			};

			web.multiSelect.unselect = function(a, s) {
				var selected = s.getValue();
				if (selected.length) {
					Ext.Array.each(selected, function(item) {
						s.store.remove(s.store.getAt(s.store.findExact('id', item)));
					});
					this.filterAvailable(a, s);
				}
			};

			web.multiSelect.unselectAll = function(a, s) {
				s.store.removeAll();
				a.store.clearFilter();
				this.filterAvailable(a, s);
			};

			web.multiSelect.filterAvailable = function(a, s) {
				a.store.filterBy( function(r) {
					var keep = true;
					s.store.each( function(r2) {
						if (r.data.id == r2.data.id) {
							keep = false;
						}

					});
					return keep;
				});
				a.store.sortStore();
			};

			web.multiSelect.setHeight = function(ms, panel, fill) {
				for (var i = 0; i < ms.length; i++) {
					ms[i].setHeight(panel.getHeight() - fill);
				}
			};

			// window
			web.window = web.window || {};

			web.window.setAnchorPosition = function(w, target) {
				var vpw = ns.app.viewport.getWidth(),
					targetx = target ? target.getPosition()[0] : 4,
					winw = w.getWidth(),
					y = target ? target.getPosition()[1] + target.getHeight() + 4 : 33;

				if ((targetx + winw) > vpw) {
					w.setPosition((vpw - winw - 2), y);
				}
				else {
					w.setPosition(targetx, y);
				}
			};

			web.window.addHideOnBlurHandler = function(w) {
				var el = Ext.get(Ext.query('.x-mask')[0]);

				el.on('click', function() {
					if (w.hideOnBlur) {
						w.hide();
					}
				});

				w.hasHideOnBlurHandler = true;
			};

			web.window.addDestroyOnBlurHandler = function(w) {
				var el = Ext.get(Ext.query('.x-mask')[0]);

				el.on('click', function() {
					if (w.destroyOnBlur) {
						w.destroy();
					}
				});

				w.hasDestroyOnBlurHandler = true;
			};

			// message
			web.message = web.message || {};

			web.message.alert = function(message) {
				alert(message);
			};

			// url
			web.url = web.url || {};

			web.url.getParam = function(s) {
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
			};

			// storage
			web.storage = web.storage || {};

				// internal
			web.storage.internal = web.storage.internal || {};

			web.storage.internal.add = function(store, storage, parent, records) {
				if (!Ext.isObject(store)) {
					console.log('support.storeage.add: store is not an object');
					return null;
				}

				storage = storage || store.storage;
				parent = parent || store.parent;

				if (!Ext.isObject(storage)) {
					console.log('support.storeage.add: storage is not an object');
					return null;
				}

				store.each( function(r) {
					if (storage[r.data.id]) {
						storage[r.data.id] = {id: r.data.id, name: r.data.name, parent: parent};
					}
				});

				if (support.prototype.array.getLength(records, true)) {
					Ext.Array.each(records, function(r) {
						if (storage[r.data.id]) {
							storage[r.data.id] = {id: r.data.id, name: r.data.name, parent: parent};
						}
					});
				}
			};

			web.storage.internal.load = function(store, storage, parent, records) {
				var a = [];

				if (!Ext.isObject(store)) {
					console.log('support.storeage.load: store is not an object');
					return null;
				}

				storage = storage || store.storage;
				parent = parent || store.parent;

				store.removeAll();

				for (var key in storage) {
					var record = storage[key];

					if (storage.hasOwnProperty(key) && record.parent === parent) {
						a.push(record);
					}
				}

				if (support.prototype.array.getLength(records)) {
					a = a.concat(records);
				}

				store.add(a);
				store.sort('name', 'ASC');
			};

				// session
			web.storage.session = web.storage.session || {};

			web.storage.session.set = function(layout, session, url) {
				if (NS.isSessionStorage) {
					var dhis2 = JSON.parse(sessionStorage.getItem('dhis2')) || {};
					dhis2[session] = layout;
					sessionStorage.setItem('dhis2', JSON.stringify(dhis2));

					if (Ext.isString(url)) {
						window.location.href = url;
					}
				}
			};

			// mouse events
			web.events = web.events || {};

			web.events.setValueMouseHandlers = function(layout, response, uuidDimUuidsMap, uuidObjectMap) {
				var valueEl;

				for (var key in uuidDimUuidsMap) {
					if (uuidDimUuidsMap.hasOwnProperty(key)) {
						valueEl = Ext.get(key);

						if (parseFloat(valueEl.dom.textContent)) {
							valueEl.dom.onValueMouseClick = web.events.onValueMouseClick;
							valueEl.dom.onValueMouseOver = web.events.onValueMouseOver;
							valueEl.dom.onValueMouseOut = web.events.onValueMouseOut;
							valueEl.dom.layout = layout;
							valueEl.dom.response = response;
							valueEl.dom.uuidDimUuidsMap = uuidDimUuidsMap;
							valueEl.dom.uuidObjectMap = uuidObjectMap;
							valueEl.dom.setAttribute('onclick', 'this.onValueMouseClick(this.layout, this.response, this.uuidDimUuidsMap, this.uuidObjectMap, this.id);');
							valueEl.dom.setAttribute('onmouseover', 'this.onValueMouseOver(this);');
							valueEl.dom.setAttribute('onmouseout', 'this.onValueMouseOut(this);');
						}
					}
				}
			};

			web.events.onValueMouseClick = function(layout, response, uuidDimUuidsMap, uuidObjectMap, uuid) {
				var uuids = uuidDimUuidsMap[uuid],
					layoutConfig = Ext.clone(layout),
					parentGraphMap = ns.app.viewport.treePanel.getParentGraphMap(),
					objects = [],
					menu;

				// modify layout dimension items based on uuid objects

				// get objects
				for (var i = 0; i < uuids.length; i++) {
					objects.push(uuidObjectMap[uuids[i]]);
				}

				// clear layoutConfig dimension items
				for (var i = 0, a = Ext.Array.clean([].concat(layoutConfig.columns || [], layoutConfig.rows || [])); i < a.length; i++) {
					a[i].items = [];
				}

				// add new items
				for (var i = 0, obj, axis; i < objects.length; i++) {
					obj = objects[i];

					axis = obj.axis === 'col' ? layoutConfig.columns || [] : layoutConfig.rows || [];

					if (axis.length) {
						axis[obj.dim].items.push({
							id: obj.id,
							name: response.metaData.names[obj.id]
						});
					}
				}

				// parent graph map
				layoutConfig.parentGraphMap = {};

				for (var i = 0, id; i < objects.length; i++) {
					id = objects[i].id;

					if (parentGraphMap.hasOwnProperty(id)) {
						layoutConfig.parentGraphMap[id] = parentGraphMap[id];
					}
				}

				// menu
				menu = Ext.create('Ext.menu.Menu', {
					shadow: true,
					showSeparator: false,
					items: [
						{
							text: 'Open selection as chart' + '&nbsp;&nbsp;', //i18n
							iconCls: 'ns-button-icon-chart',
							param: 'chart',
							handler: function() {
								web.storage.session.set(layoutConfig, 'analytical', init.contextPath + '/dhis-web-visualizer/app/index.html?s=analytical');
							},
							listeners: {
								render: function() {
									this.getEl().on('mouseover', function() {
										web.events.onValueMenuMouseHover(uuidDimUuidsMap, uuid, 'mouseover', 'chart');
									});

									this.getEl().on('mouseout', function() {
										web.events.onValueMenuMouseHover(uuidDimUuidsMap, uuid, 'mouseout', 'chart');
									});
								}
							}
						},
						{
							text: 'Open selection as map' + '&nbsp;&nbsp;', //i18n
							iconCls: 'ns-button-icon-map',
							param: 'map',
							disabled: true,
							handler: function() {
								web.storage.session.set(layoutConfig, 'analytical', init.contextPath + '/dhis-web-mapping/app/index.html?s=analytical');
							},
							listeners: {
								render: function() {
									this.getEl().on('mouseover', function() {
										web.events.onValueMenuMouseHover(uuidDimUuidsMap, uuid, 'mouseover', 'map');
									});

									this.getEl().on('mouseout', function() {
										web.events.onValueMenuMouseHover(uuidDimUuidsMap, uuid, 'mouseout', 'map');
									});
								}
							}
						}
					]
				});

				menu.showAt(function() {
					var el = Ext.get(uuid),
						xy = el.getXY();

					xy[0] += el.getWidth() - 5;
					xy[1] += el.getHeight() - 5;

					return xy;
				}());
			};

			web.events.onValueMouseOver = function(uuid) {
				Ext.get(uuid).addCls('highlighted');
			};

			web.events.onValueMouseOut = function(uuid) {
				Ext.get(uuid).removeCls('highlighted');
			};

			web.events.onValueMenuMouseHover = function(uuidDimUuidsMap, uuid, event, param) {
				var dimUuids;

				// dimension elements
				if (param === 'chart') {
					if (Ext.isString(uuid) && Ext.isArray(uuidDimUuidsMap[uuid])) {
						dimUuids = uuidDimUuidsMap[uuid];

						for (var i = 0, el; i < dimUuids.length; i++) {
							el = Ext.get(dimUuids[i]);

							if (el) {
								if (event === 'mouseover') {
									el.addCls('highlighted');
								}
								else if (event === 'mouseout') {
									el.removeCls('highlighted');
								}
							}
						}
					}
				}
			};

			web.events.setColumnHeaderMouseHandlers = function(xLayout, response) {
				if (Ext.isArray(xLayout.sortableIdObjects)) {
					for (var i = 0, obj, el; i < xLayout.sortableIdObjects.length; i++) {
						obj = xLayout.sortableIdObjects[i];
						el = Ext.get(obj.uuid);

						el.dom.xLayout = xLayout;
						el.dom.response = response;
						el.dom.metaDataId = obj.id;
						el.dom.onColumnHeaderMouseClick = web.events.onColumnHeaderMouseClick;
						el.dom.onColumnHeaderMouseOver = web.events.onColumnHeaderMouseOver;
						el.dom.onColumnHeaderMouseOut = web.events.onColumnHeaderMouseOut;

						el.dom.setAttribute('onclick', 'this.onColumnHeaderMouseClick(this.xLayout, this.response, this.metaDataId)');
						el.dom.setAttribute('onmouseover', 'this.onColumnHeaderMouseOver(this)');
						el.dom.setAttribute('onmouseout', 'this.onColumnHeaderMouseOut(this)');
					}
				}
			};

			web.events.onColumnHeaderMouseClick = function(xLayout, response, id) {
				if (xLayout.sorting && xLayout.sorting.id === id) {
					xLayout.sorting.direction = support.prototype.str.toggleDirection(xLayout.sorting.direction);
				}
				else {
					xLayout.sorting = {
						id: id,
						direction: 'DESC'
					};
				}

				ns.core.web.pivot.sort(xLayout, response, id);
			};

			web.events.onColumnHeaderMouseOver = function(el) {
				Ext.get(el).addCls('pointer highlighted');
			};

			web.events.onColumnHeaderMouseOut = function(el) {
				Ext.get(el).removeCls('pointer highlighted');
			};

			// pivot
			web.pivot = web.pivot || {};

			web.pivot.getLayoutConfig = function() {
				var panels = ns.app.accordion.panels,
					columnDimNames = ns.app.stores.col.getDimensionNames(),
					rowDimNames = ns.app.stores.row.getDimensionNames(),
					filterDimNames = ns.app.stores.filter.getDimensionNames(),
					config = ns.app.optionsWindow.getOptions(),
					dx = dimConf.data.dimensionName,
					co = dimConf.category.dimensionName,
					nameDimArrayMap = {};

				config.columns = [];
				config.rows = [];
				config.filters = [];

				// panel data
				for (var i = 0, dim, dimName; i < panels.length; i++) {
					dim = panels[i].getDimension();

					if (dim) {
						nameDimArrayMap[dim.dimension] = [dim];
					}
				}

				nameDimArrayMap[dx] = Ext.Array.clean([].concat(
					nameDimArrayMap[dimConf.indicator.objectName] || [],
					nameDimArrayMap[dimConf.dataElement.objectName] || [],
					nameDimArrayMap[dimConf.operand.objectName] || [],
					nameDimArrayMap[dimConf.dataSet.objectName] || []
				));

				// columns, rows, filters
				for (var i = 0, nameArrays = [columnDimNames, rowDimNames, filterDimNames], axes = [config.columns, config.rows, config.filters], dimNames; i < nameArrays.length; i++) {
					dimNames = nameArrays[i];

					for (var j = 0, dimName, dim; j < dimNames.length; j++) {
						dimName = dimNames[j];

						if (dimName === co) {
							axes[i].push({
								dimension: co,
								items: []
							});
						}
						else if (dimName === dx && nameDimArrayMap.hasOwnProperty(dimName) && nameDimArrayMap[dimName]) {
							for (var k = 0; k < nameDimArrayMap[dx].length; k++) {
								axes[i].push(Ext.clone(nameDimArrayMap[dx][k]));
							}
						}
						else if (nameDimArrayMap.hasOwnProperty(dimName) && nameDimArrayMap[dimName]) {
							for (var k = 0; k < nameDimArrayMap[dimName].length; k++) {
								axes[i].push(Ext.clone(nameDimArrayMap[dimName][k]));
							}
						}
					}
				}

				return config;
			};

			web.pivot.loadTable = function(id) {
				if (!Ext.isString(id)) {
					alert('Invalid report table id');
					return;
				}

				Ext.Ajax.request({
					url: init.contextPath + '/api/reportTables/' + id + '.json?viewClass=dimensional&links=false',
					failure: function(r) {
						web.mask.hide(ns.app.centerRegion);
						alert(r.responseText);
					},
					success: function(r) {
						var layoutConfig = Ext.decode(r.responseText),
							layout = api.layout.Layout(layoutConfig);

						if (layout) {
							web.pivot.getData(layout, true);
						}
					}
				});
			};

			web.pivot.getData = function(layout, isUpdateGui) {
				var xLayout,
					paramString;

				if (!layout) {
					return;
				}

				xLayout = service.layout.getExtendedLayout(layout);
				paramString = web.analytics.getParamString(xLayout, true);

				// show mask
				web.mask.show(ns.app.centerRegion);

				Ext.Ajax.request({
					url: init.contextPath + '/api/analytics.json' + paramString,
					timeout: 60000,
					headers: {
						'Content-Type': 'application/json',
						'Accepts': 'application/json'
					},
					disableCaching: false,
					failure: function(r) {
						ns.app.viewport.setGui(layout, xLayout, isUpdateGui);
						
						web.mask.hide(ns.app.centerRegion);

						if (r.status === 413 || r.status === 414) {
							web.analytics.validateUrl(init.contextPath + '/api/analytics.json' + paramString);
						}
						else {
							alert(r.responseText);
						}
					},
					success: function(r) {
						var response = api.response.Response(Ext.decode(r.responseText));

						if (!response) {
							ns.app.viewport.setGui(layout, xLayout, isUpdateGui);
							web.mask.hide(ns.app.centerRegion);
							return;
						}

						// sync xLayout with response
						xLayout = service.layout.getSyncronizedXLayout(xLayout, response);

						if (!xLayout) {
							web.mask.hide(ns.app.centerRegion);
							return;
						}

						ns.app.paramString = paramString;

						web.pivot.createTable(layout, xLayout, response, isUpdateGui);
					}
				});
			};

			web.pivot.createTable = function(layout, xLayout, response, isUpdateGui) {
				var xResponse,
					xColAxis,
					xRowAxis,
					config;

				if (!xLayout) {
					xLayout = service.layout.getExtendedLayout(layout);
				}

				// extend response
				xResponse = service.response.getExtendedResponse(xLayout, response);

				// extended axes
				xColAxis = service.layout.getExtendedAxis(xLayout, xResponse, 'col');
				xRowAxis = service.layout.getExtendedAxis(xLayout, xResponse, 'row');

				// update viewport
				config = web.pivot.getHtml(xLayout, xResponse, xColAxis, xRowAxis);
				ns.app.centerRegion.removeAll(true);
				ns.app.centerRegion.update(config.html);

				// after render
				ns.app.layout = layout;
				ns.app.xLayout = xLayout;
				ns.app.response = response;
				ns.app.xResponse = xResponse;
				ns.app.uuidDimUuidsMap = config.uuidDimUuidsMap;
				ns.app.uuidObjectMap = Ext.applyIf((xColAxis ? xColAxis.uuidObjectMap : {}), (xRowAxis ? xRowAxis.uuidObjectMap : {}));

				if (NS.isSessionStorage) {
					web.events.setValueMouseHandlers(layout, response, ns.app.uuidDimUuidsMap, ns.app.uuidObjectMap);
					web.events.setColumnHeaderMouseHandlers(xLayout, response);
					web.storage.session.set(layout, 'table');
				}

				ns.app.viewport.setGui(layout, xLayout, isUpdateGui);

				web.mask.hide(ns.app.centerRegion);

				if (NS.isDebug) {
					console.log("core", ns.core);
					console.log("app", ns.app);
				}
			};

			web.pivot.sort = function(xLayout, response, id) {
				var xLayout = Ext.clone(xLayout),
					response = Ext.clone(response),
					dim = xLayout.rows[0],
					valueMap = response.idValueMap,
					direction = xLayout.sorting ? xLayout.sorting.direction : 'DESC',
					layout;

				dim.ids = [];

				// collect values
				for (var i = 0, item, key, value; i < dim.items.length; i++) {
					item = dim.items[i];
					key = id + item.id;
					value = parseFloat(valueMap[key]);

					item.value = Ext.isNumber(value) ? value : (Number.MAX_VALUE * -1);
				}

				// sort
				support.prototype.array.sort(dim.items, direction, 'value');

				// new id order
				for (var i = 0; i < dim.items.length; i++) {
					dim.ids.push(dim.items[i].id);
				}

				// re-layout
				layout = api.layout.Layout(xLayout);

				// re-create table
				web.pivot.createTable(layout, null, response, false);
			};
		}());
	};

	// viewport
	createViewport = function() {
        var indicatorAvailableStore,
			indicatorSelectedStore,
			dataElementAvailableStore,
			dataElementSelectedStore,
			dataElementGroupStore,
			dataSetAvailableStore,
			dataSetSelectedStore,
			periodTypeStore,
			fixedPeriodAvailableStore,
			fixedPeriodSelectedStore,
			reportTableStore,
			organisationUnitLevelStore,
			organisationUnitGroupStore,
			legendSetStore,

			indicatorAvailable,
			indicatorSelected,
			indicator,
			dataElementAvailable,
			dataElementSelected,
			dataElement,
			dataSetAvailable,
			dataSetSelected,
			dataSet,
			rewind,
			relativePeriod,
			fixedPeriodAvailable,
			fixedPeriodSelected,
			period,
			treePanel,
			userOrganisationUnit,
			userOrganisationUnitChildren,
			userOrganisationUnitGrandChildren,
			userOrganisationUnitPanel,
			organisationUnitLevel,
			tool,
			toolPanel,
			organisationUnit,
			dimensionIdAvailableStoreMap = {},
			dimensionIdSelectedStoreMap = {},
			getGroupSetPanels,
			update,

			layoutButton,
			optionsButton,
			favoriteButton,
			openTableLayoutTab,
			downloadButton,
			interpretationItem,
			pluginItem,
			shareButton,

			accordionBody,
			accordion,
			westRegion,
			centerRegion,

			setGui,
			viewport,

			accordionPanels = [];

		ns.app.stores = ns.app.stores || {};

		indicatorAvailableStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				reader: {
					type: 'json',
					root: 'indicators'
				},
				pageParam: false,
				startParam: false,
				limitParam: false
			},
			storage: {},
			parent: null,
			sortStore: function() {
				this.sort('name', 'ASC');
			},
			listeners: {
				load: function(s) {
					ns.core.web.storage.internal.add(s);
					ns.core.web.multiSelect.filterAvailable({store: s}, {store: indicatorSelectedStore});
				}
			}
		});
		ns.app.stores.indicatorAvailable = indicatorAvailableStore;

		indicatorSelectedStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: []
		});
		ns.app.stores.indicatorSelected = indicatorSelectedStore;

		indicatorGroupStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'index'],
			proxy: {
				type: 'ajax',
				url: ns.core.init.contextPath + '/api/indicatorGroups.json?paging=false&links=false',
				reader: {
					type: 'json',
					root: 'indicatorGroups'
				},
				pageParam: false,
				startParam: false,
				limitParam: false
			},
			listeners: {
				load: function(s) {
					s.add({
						id: 0,
						name: '[ ' + NS.i18n.all_indicators + ' ]',
						index: -1
					});
					s.sort([
						{
							property: 'index',
							direction: 'ASC'
						},
						{
							property: 'name',
							direction: 'ASC'
						}
					]);
				}
			}
		});
		ns.app.stores.indicatorGroup = indicatorGroupStore;

		dataElementAvailableStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				reader: {
					type: 'json',
					root: 'dataElements'
				}
			},
			storage: {},
			sortStore: function() {
				this.sort('name', 'ASC');
			},
			setTotalsProxy: function(uid) {
				var params = 'domainType=aggregate&links=false&paging=false',
					path;

				if (Ext.isString(uid)) {
					path = '/dataElementGroups/' + uid + '.json?' + params;
				}
				else if (uid === 0) {
					path = '/dataElements.json?' + params;
				}

				if (!path) {
					alert('Available data elements: invalid id');
					return;
				}

				this.setProxy({
					type: 'ajax',
					url: ns.core.init.contextPath + '/api' + path,
					reader: {
						type: 'json',
						root: 'dataElements'
					},
					pageParam: false,
					startParam: false,
					limitParam: false
				});

				this.load({
					scope: this,
					callback: function() {
						ns.core.web.multiSelect.filterAvailable({store: dataElementAvailableStore}, {store: dataElementSelectedStore});
					}
				});
			},
			setDetailsProxy: function(uid) {
				if (Ext.isString(uid)) {
					this.setProxy({
						type: 'ajax',
						url: ns.core.init.contextPath + '/api/generatedDataElementOperands.json?paging=false&links=false&dataElementGroup=' + uid,
						reader: {
							type: 'json',
							root: 'dataElementOperands'
						},
						pageParam: false,
						startParam: false,
						limitParam: false
					});

					this.load({
						scope: this,
						callback: function() {
							this.each(function(r) {
								r.set('id', r.data.id.split('.').join('-'));
							});

							ns.core.web.multiSelect.filterAvailable({store: dataElementAvailableStore}, {store: dataElementSelectedStore});
						}
					});
				}
				else {
					this.removeAll();
                    dataElementGroupComboBox.clearValue();
				}
			},
			listeners: {
				load: function(s) {
					ns.core.web.storage.internal.add(s);
					ns.core.web.multiSelect.filterAvailable({store: s}, {store: dataElementSelectedStore});
				}
			}
		});
		ns.app.stores.dataElementAvailable = dataElementAvailableStore;

		dataElementSelectedStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: []
		});
		ns.app.stores.dataElementSelected = dataElementSelectedStore;

		dataElementGroupStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'index'],
			proxy: {
				type: 'ajax',
				url: ns.core.init.contextPath + '/api/dataElementGroups.json?paging=false&links=false',
				reader: {
					type: 'json',
					root: 'dataElementGroups'
				},
				pageParam: false,
				startParam: false,
				limitParam: false
			},
			listeners: {
				load: function(s) {
					if (dataElementDetailLevel.getValue() === ns.core.conf.finals.dimension.dataElement.objectName) {
						s.add({
							id: 0,
							name: '[ ' + NS.i18n.all_data_element_groups + ' ]',
							index: -1
						});
					}

					s.sort([
						{property: 'index', direction: 'ASC'},
						{property: 'name', direction: 'ASC'}
					]);
				}
			}
		});
		ns.app.stores.dataElementGroup = dataElementGroupStore;

		dataSetAvailableStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: ns.core.init.contextPath + '/api/dataSets.json?paging=false&links=false',
				reader: {
					type: 'json',
					root: 'dataSets'
				},
				pageParam: false,
				startParam: false,
				limitParam: false
			},
			storage: {},
			sortStore: function() {
				this.sort('name', 'ASC');
			},
			isLoaded: false,
			listeners: {
				load: function(s) {
					this.isLoaded = true;

					ns.core.web.storage.internal.add(s);
					ns.core.web.multiSelect.filterAvailable({store: s}, {store: dataSetSelectedStore});
				}
			}
		});
		ns.app.stores.dataSetAvailable = dataSetAvailableStore;

		dataSetSelectedStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: []
		});
		ns.app.stores.dataSetSelected = dataSetSelectedStore;

		periodTypeStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: ns.core.conf.period.periodTypes
		});
		ns.app.stores.periodType = periodTypeStore;

		fixedPeriodAvailableStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'index'],
			data: [],
			setIndex: function(periods) {
				for (var i = 0; i < periods.length; i++) {
					periods[i].index = i;
				}
			},
			sortStore: function() {
				this.sort('index', 'ASC');
			}
		});
		ns.app.stores.fixedPeriodAvailable = fixedPeriodAvailableStore;

		fixedPeriodSelectedStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: []
		});
		ns.app.stores.fixedPeriodSelected = fixedPeriodSelectedStore;

		reportTableStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'lastUpdated', 'access'],
			proxy: {
				type: 'ajax',
				reader: {
					type: 'json',
					root: 'reportTables'
				}
			},
			isLoaded: false,
			pageSize: 10,
			page: 1,
			defaultUrl: ns.core.init.contextPath + '/api/reportTables.json?viewClass=sharing&links=false',
			loadStore: function(url) {
				this.proxy.url = url || this.defaultUrl;

				this.load({
					params: {
						pageSize: this.pageSize,
						page: this.page
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
			listeners: {
				load: function(s) {
					if (!this.isLoaded) {
						this.isLoaded = true;
					}

					this.sort('name', 'ASC');
				}
			}
		});
		ns.app.stores.reportTable = reportTableStore;

		organisationUnitLevelStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'level'],
			data: ns.core.init.organisationUnitLevels
		});
		ns.app.stores.organisationUnitLevel = organisationUnitLevelStore;

		organisationUnitGroupStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: ns.core.init.contextPath + '/api/organisationUnitGroups.json?paging=false&links=false',
				reader: {
					type: 'json',
					root: 'organisationUnitGroups'
				}
			}
		});
		ns.app.stores.organisationUnitGroup = organisationUnitGroupStore;

		legendSetStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'index'],
			data: function() {
				var data = ns.core.init.legendSets;
				data.unshift({id: 0, name: NS.i18n.none, index: -1});
				return data;
			}(),
			sorters: [
				{property: 'index', direction: 'ASC'},
				{property: 'name', direction: 'ASC'}
			]
		});
		ns.app.stores.legendSet = legendSetStore;

		// data
		indicatorAvailable = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-left',
			width: (ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			store: indicatorAvailableStore,
			tbar: [
				{
					xtype: 'label',
					text: NS.i18n.available,
					cls: 'ns-toolbar-multiselect-left-label'
				},
				'->',
				{
					xtype: 'button',
					icon: 'images/arrowright.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.select(indicatorAvailable, indicatorSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowrightdouble.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.selectAll(indicatorAvailable, indicatorSelected);
					}
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.core.web.multiSelect.select(this, indicatorSelected);
					}, this);
				}
			}
		});

		indicatorSelected = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-right',
			width: (ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			ddReorder: true,
			store: indicatorSelectedStore,
			tbar: [
				{
					xtype: 'button',
					icon: 'images/arrowleftdouble.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.unselectAll(indicatorAvailable, indicatorSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowleft.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.unselect(indicatorAvailable, indicatorSelected);
					}
				},
				'->',
				{
					xtype: 'label',
					text: NS.i18n.selected,
					cls: 'ns-toolbar-multiselect-right-label'
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.core.web.multiSelect.unselect(indicatorAvailable, this);
					}, this);
				}
			}
		});

		indicator = {
			xtype: 'panel',
			title: '<div class="ns-panel-title-data">' + NS.i18n.indicators + '</div>',
			hideCollapseTool: true,
			getDimension: function() {
				var config = {
					dimension: dimConf.indicator.objectName,
					items: []
				};

				indicatorSelectedStore.each( function(r) {
					config.items.push({
						id: r.data.id,
						name: r.data.name
					});
				});

				return config.items.length ? config : null;
			},
			onExpand: function() {
				var h = westRegion.hasScrollbar ?
					ns.core.conf.layout.west_scrollbarheight_accordion_indicator : ns.core.conf.layout.west_maxheight_accordion_indicator;
				accordion.setThisHeight(h);
				ns.core.web.multiSelect.setHeight(
					[indicatorAvailable, indicatorSelected],
					this,
					ns.core.conf.layout.west_fill_accordion_indicator
				);
			},
			items: [
				{
					xtype: 'combobox',
					cls: 'ns-combo',
					style: 'margin-bottom:2px; margin-top:0px',
					width: ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding,
					valueField: 'id',
					displayField: 'name',
					emptyText: NS.i18n.select_indicator_group,
					editable: false,
					store: indicatorGroupStore,
					listeners: {
						select: function(cb) {
							var store = indicatorAvailableStore,
								id = cb.getValue();

							store.parent = id;

							if (ns.core.support.prototype.object.hasObject(store.storage, 'parent', id)) {
								ns.core.web.storage.internal.load(store);
								ns.core.web.multiSelect.filterAvailable(indicatorAvailable, indicatorSelected);
							}
							else {
								if (id === 0) {
									store.proxy.url = ns.core.init.contextPath + '/api/indicators.json?paging=false&links=false';
									store.load();
								}
								else {
									store.proxy.url = ns.core.init.contextPath + '/api/indicatorGroups/' + id + '.json';
									store.load();
								}
							}
						}
					}
				},
				{
					xtype: 'panel',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						indicatorAvailable,
						indicatorSelected
					]
				}
			],
			listeners: {
				added: function() {
					accordionPanels.push(this);
				},
				expand: function(p) {
					p.onExpand();
				}
			}
		};

		dataElementAvailable = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-left',
			width: (ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			store: dataElementAvailableStore,
			tbar: [
				{
					xtype: 'label',
					text: NS.i18n.available,
					cls: 'ns-toolbar-multiselect-left-label'
				},
				'->',
				{
					xtype: 'button',
					icon: 'images/arrowright.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.select(dataElementAvailable, dataElementSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowrightdouble.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.selectAll(dataElementAvailable, dataElementSelected);
					}
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.core.web.multiSelect.select(this, dataElementSelected);
					}, this);
				}
			}
		});

		dataElementSelected = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-right',
			width: (ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			ddReorder: true,
			store: dataElementSelectedStore,
			tbar: [
				{
					xtype: 'button',
					icon: 'images/arrowleftdouble.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.unselectAll(dataElementAvailable, dataElementSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowleft.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.unselect(dataElementAvailable, dataElementSelected);
					}
				},
				'->',
				{
					xtype: 'label',
					text: NS.i18n.selected,
					cls: 'ns-toolbar-multiselect-right-label'
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.core.web.multiSelect.unselect(dataElementAvailable, this);
					}, this);
				}
			}
		});

		dataElementGroupComboBox = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			style: 'margin:0 2px 2px 0',
			width: ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding - 90,
			valueField: 'id',
			displayField: 'name',
			emptyText: NS.i18n.select_data_element_group,
			editable: false,
			store: dataElementGroupStore,
			loadAvailable: function() {
				var store = dataElementAvailableStore,
					detailLevel = dataElementDetailLevel.getValue(),
					value = this.getValue();

				if (value !== null) {
					if (detailLevel === dimConf.dataElement.objectName) {
						store.setTotalsProxy(value);
					}
					else {
						store.setDetailsProxy(value);
					}
				}
			},
			listeners: {
				select: function(cb) {
					cb.loadAvailable();
				}
			}
		});

		dataElementDetailLevel = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			style: 'margin-bottom:2px',
			baseBodyCls: 'small',
			queryMode: 'local',
			editable: false,
			valueField: 'id',
			displayField: 'text',
			width: 90 - 2,
			value: dimConf.dataElement.objectName,
			store: {
				fields: ['id', 'text'],
				data: [
					{id: ns.core.conf.finals.dimension.dataElement.objectName, text: NS.i18n.totals},
					{id: ns.core.conf.finals.dimension.operand.objectName, text: NS.i18n.details}
				]
			},
			listeners: {
				select: function(cb) {
					var record = dataElementGroupStore.getById(0);

					if (cb.getValue() === ns.core.conf.finals.dimension.operand.objectName && record) {
						dataElementGroupStore.remove(record);
					}

					if (cb.getValue() === ns.core.conf.finals.dimension.dataElement.objectName && !record) {
						dataElementGroupStore.insert(0, {
							id: 0,
							name: '[ ' + NS.i18n.all_data_element_groups + ' ]',
							index: -1
						});
					}

					dataElementGroupComboBox.loadAvailable();
					dataElementSelectedStore.removeAll();
				}
			}
		});

		dataElement = {
			xtype: 'panel',
			title: '<div class="ns-panel-title-data">' + NS.i18n.data_elements + '</div>',
			hideCollapseTool: true,
			getDimension: function() {
				var config = {
					dimension: dataElementDetailLevel.getValue(),
					items: []
				};

				dataElementSelectedStore.each( function(r) {
					config.items.push({
						id: r.data.id,
						name: r.data.name
					});
				});

				return config.items.length ? config : null;
			},
			onExpand: function() {
				var h = ns.app.westRegion.hasScrollbar ?
					ns.core.conf.layout.west_scrollbarheight_accordion_dataelement : ns.core.conf.layout.west_maxheight_accordion_dataelement;
				accordion.setThisHeight(h);
				ns.core.web.multiSelect.setHeight(
					[dataElementAvailable, dataElementSelected],
					this,
					ns.core.conf.layout.west_fill_accordion_indicator
				);
			},
			items: [
				{
					xtype: 'container',
					layout: 'column',
					items: [
						dataElementGroupComboBox,
						dataElementDetailLevel
					]
				},
				{
					xtype: 'panel',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						dataElementAvailable,
						dataElementSelected
					]
				}
			],
			listeners: {
				added: function() {
					accordionPanels.push(this);
				},
				expand: function(p) {
					p.onExpand();
				}
			}
		};

		dataSetAvailable = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-left',
			width: (ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			store: dataSetAvailableStore,
			tbar: [
				{
					xtype: 'label',
					text: NS.i18n.available,
					cls: 'ns-toolbar-multiselect-left-label'
				},
				'->',
				{
					xtype: 'button',
					icon: 'images/arrowright.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.select(dataSetAvailable, dataSetSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowrightdouble.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.selectAll(dataSetAvailable, dataSetSelected);
					}
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.core.web.multiSelect.select(this, dataSetSelected);
					}, this);
				}
			}
		});

		dataSetSelected = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-right',
			width: (ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding) / 2,
			valueField: 'id',
			displayField: 'name',
			ddReorder: true,
			store: dataSetSelectedStore,
			tbar: [
				{
					xtype: 'button',
					icon: 'images/arrowleftdouble.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.unselectAll(dataSetAvailable, dataSetSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowleft.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.unselect(dataSetAvailable, dataSetSelected);
					}
				},
				'->',
				{
					xtype: 'label',
					text: NS.i18n.selected,
					cls: 'ns-toolbar-multiselect-right-label'
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.core.web.multiSelect.unselect(dataSetAvailable, this);
					}, this);
				}
			}
		});

		dataSet = {
			xtype: 'panel',
			title: '<div class="ns-panel-title-data">' + NS.i18n.reporting_rates + '</div>',
			hideCollapseTool: true,
			getDimension: function() {
				var config = {
					dimension: dimConf.dataSet.objectName,
					items: []
				};

				dataSetSelectedStore.each( function(r) {
					config.items.push({
						id: r.data.id,
						name: r.data.name
					});
				});

				return config.items.length ? config : null;
			},
			onExpand: function() {
				var h = ns.app.westRegion.hasScrollbar ?
					ns.core.conf.layout.west_scrollbarheight_accordion_dataset : ns.core.conf.layout.west_maxheight_accordion_dataset;
				accordion.setThisHeight(h);
				ns.core.web.multiSelect.setHeight(
					[dataSetAvailable, dataSetSelected],
					this,
					ns.core.conf.layout.west_fill_accordion_dataset
				);

				if (!dataSetAvailableStore.isLoaded) {
					dataSetAvailableStore.load();
				}
			},
			items: [
				{
					xtype: 'panel',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						dataSetAvailable,
						dataSetSelected
					]
				}
			],
			listeners: {
				added: function() {
					accordionPanels.push(this);
				},
				expand: function(p) {
					p.onExpand();
				}
			}
		};

		// period
		rewind = Ext.create('Ext.form.field.Checkbox', {
			relativePeriodId: 'rewind',
			boxLabel: 'Rewind one period',
			xable: function() {
				this.setDisabled(period.isNoRelativePeriods());
			}
		});

		relativePeriod = {
			xtype: 'panel',
			hideCollapseTool: true,
			autoScroll: true,
			bodyStyle: 'border:0 none',
			valueComponentMap: {},
			items: [
				{
					xtype: 'container',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
//						{
//							xtype: 'panel',
//							columnWidth: 0.34,
//							bodyStyle: 'border-style:none; padding:0 0 0 8px',
//							defaults: {
//								labelSeparator: '',
//								style: 'margin-bottom:2px',
//								listeners: {
//									added: function(chb) {
//										if (chb.xtype === 'checkbox') {
//											period.checkboxes.push(chb);
//											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
//										}
//									},
//									change: function() {
//										//rewind.xable();
//									}
//								}
//							},
//							items: [
//								{
//									xtype: 'label',
//									text: NS.i18n.weeks,
//									cls: 'ns-label-period-heading'
//								},
//								{
//									xtype: 'checkbox',
//									relativePeriodId: 'LAST_WEEK',
//									boxLabel: NS.i18n.last_week
//								},
//								{
//									xtype: 'checkbox',
//									relativePeriodId: 'LAST_4_WEEKS',
//									boxLabel: NS.i18n.last_4_weeks
//								},
//								{
//									xtype: 'checkbox',
//									relativePeriodId: 'LAST_12_WEEKS',
//									boxLabel: NS.i18n.last_12_weeks
//								}
//							]
//						},
						{
							xtype: 'panel',
							columnWidth: 0.33,
							bodyStyle: 'border-style:none',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											period.checkboxes.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										//rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: NS.i18n.months,
									cls: 'ns-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_MONTH',
									boxLabel: NS.i18n.last_month
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_3_MONTHS',
									boxLabel: NS.i18n.last_3_months
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_12_MONTHS',
									boxLabel: NS.i18n.last_12_months,
									checked: true
								}
							]
						}//,
//						{
//							xtype: 'panel',
//							columnWidth: 0.33,
//							bodyStyle: 'border-style:none',
//							defaults: {
//								labelSeparator: '',
//								style: 'margin-bottom:2px',
//								listeners: {
//									added: function(chb) {
//										if (chb.xtype === 'checkbox') {
//											period.checkboxes.push(chb);
//											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
//										}
//									},
//									change: function() {
//										//rewind.xable();
//									}
//								}
//							},
//							items: [
//								{
//									xtype: 'label',
//									text: NS.i18n.bimonths,
//									cls: 'ns-label-period-heading'
//								},
//								{
//									xtype: 'checkbox',
//									relativePeriodId: 'LAST_BIMONTH',
//									boxLabel: NS.i18n.last_bimonth
//								},
//								{
//									xtype: 'checkbox',
//									relativePeriodId: 'LAST_6_BIMONTHS',
//									boxLabel: NS.i18n.last_6_bimonths
//								}
//							]
//						}
					]
				},
				{
					xtype: 'container',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						{
							xtype: 'panel',
							columnWidth: 0.34,
							bodyStyle: 'border-style:none; padding:5px 0 0 10px',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											period.checkboxes.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										//rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: NS.i18n.quarters,
									cls: 'ns-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_QUARTER',
									boxLabel: NS.i18n.last_quarter
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_4_QUARTERS',
									boxLabel: NS.i18n.last_4_quarters
								}
							]
						},
//						{
//							xtype: 'panel',
//							columnWidth: 0.33,
//							bodyStyle: 'border-style:none; padding:5px 0 0',
//							defaults: {
//								labelSeparator: '',
//								style: 'margin-bottom:2px',
//								listeners: {
//									added: function(chb) {
//										if (chb.xtype === 'checkbox') {
//											period.checkboxes.push(chb);
//											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
//										}
//									},
//									change: function() {
//										//rewind.xable();
//									}
//								}
//							},
//							items: [
//								{
//									xtype: 'label',
//									text: NS.i18n.sixmonths,
//									cls: 'ns-label-period-heading'
//								},
//								{
//									xtype: 'checkbox',
//									relativePeriodId: 'LAST_SIX_MONTH',
//									boxLabel: NS.i18n.last_sixmonth
//								},
//								{
//									xtype: 'checkbox',
//									relativePeriodId: 'LAST_2_SIXMONTHS',
//									boxLabel: NS.i18n.last_2_sixmonths
//								}
//							]
//						},
						{
							xtype: 'panel',
							columnWidth: 0.33,
							bodyStyle: 'border-style:none; padding:5px 0 0',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											period.checkboxes.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										//rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: NS.i18n.financial_years,
									cls: 'ns-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_FINANCIAL_YEAR',
									boxLabel: NS.i18n.last_financial_year
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_5_FINANCIAL_YEARS',
									boxLabel: NS.i18n.last_5_financial_years
								}
							]
						}

						//{
							//xtype: 'panel',
							//layout: 'anchor',
							//bodyStyle: 'border-style:none; padding:5px 0 0 46px',
							//defaults: {
								//labelSeparator: '',
								//style: 'margin-bottom:2px',
							//},
							//items: [
								//{
									//xtype: 'label',
									//text: 'Options',
									//cls: 'ns-label-period-heading-options'
								//},
								//rewind
							//]
						//}
					]
				},
				{
					xtype: 'container',
					layout: 'column',
					bodyStyle: 'border-style:none',
					items: [
						{
							xtype: 'panel',
							columnWidth: 0.35,
							bodyStyle: 'border-style:none; padding:5px 0 0 10px',
							defaults: {
								labelSeparator: '',
								style: 'margin-bottom:2px',
								listeners: {
									added: function(chb) {
										if (chb.xtype === 'checkbox') {
											period.checkboxes.push(chb);
											relativePeriod.valueComponentMap[chb.relativePeriodId] = chb;
										}
									},
									change: function() {
										//rewind.xable();
									}
								}
							},
							items: [
								{
									xtype: 'label',
									text: NS.i18n.years,
									cls: 'ns-label-period-heading'
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'THIS_YEAR',
									boxLabel: NS.i18n.this_year
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_YEAR',
									boxLabel: NS.i18n.last_year
								},
								{
									xtype: 'checkbox',
									relativePeriodId: 'LAST_5_YEARS',
									boxLabel: NS.i18n.last_5_years
								}
							]
						}
					]
				}
			]
		};

		fixedPeriodAvailable = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-left',
			width: (ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding) / 2,
			height: 180,
			valueField: 'id',
			displayField: 'name',
			store: fixedPeriodAvailableStore,
			tbar: [
				{
					xtype: 'label',
					text: NS.i18n.available,
					cls: 'ns-toolbar-multiselect-left-label'
				},
				'->',
				{
					xtype: 'button',
					icon: 'images/arrowright.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.select(fixedPeriodAvailable, fixedPeriodSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowrightdouble.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.selectAll(fixedPeriodAvailable, fixedPeriodSelected, true);
					}
				},
				' '
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.core.web.multiSelect.select(fixedPeriodAvailable, fixedPeriodSelected);
					}, this);
				}
			}
		});

		fixedPeriodSelected = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'ns-toolbar-multiselect-right',
			width: (ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding) / 2,
			height: 180,
			valueField: 'id',
			displayField: 'name',
			ddReorder: false,
			store: fixedPeriodSelectedStore,
			tbar: [
				' ',
				{
					xtype: 'button',
					icon: 'images/arrowleftdouble.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.unselectAll(fixedPeriodAvailable, fixedPeriodSelected);
					}
				},
				{
					xtype: 'button',
					icon: 'images/arrowleft.png',
					width: 22,
					handler: function() {
						ns.core.web.multiSelect.unselect(fixedPeriodAvailable, fixedPeriodSelected);
					}
				},
				'->',
				{
					xtype: 'label',
					text: NS.i18n.selected,
					cls: 'ns-toolbar-multiselect-right-label'
				}
			],
			listeners: {
				afterrender: function() {
					this.boundList.on('itemdblclick', function() {
						ns.core.web.multiSelect.unselect(fixedPeriodAvailable, fixedPeriodSelected);
					}, this);
				}
			}
		});

		period = {
			xtype: 'panel',
			title: '<div class="ns-panel-title-period">Periods</div>',
			hideCollapseTool: true,
			checkboxes: [],
			getDimension: function() {
				var config = {
						dimension: dimConf.period.objectName,
						items: []
					};

				fixedPeriodSelectedStore.each( function(r) {
					config.items.push({
						id: r.data.id,
						name: r.data.name
					});
				});

				for (var i = 0; i < this.checkboxes.length; i++) {
					if (this.checkboxes[i].getValue()) {
						config.items.push({
							id: this.checkboxes[i].relativePeriodId,
							name: ''
						});
					}
				}

				return config.items.length ? config : null;
			},
			onExpand: function() {
				var h = ns.app.westRegion.hasScrollbar ?
					ns.core.conf.layout.west_scrollbarheight_accordion_period : ns.core.conf.layout.west_maxheight_accordion_period;
				accordion.setThisHeight(h);
				ns.core.web.multiSelect.setHeight(
					[fixedPeriodAvailable, fixedPeriodSelected],
					this,
					ns.core.conf.layout.west_fill_accordion_period
				);
			},
			resetRelativePeriods: function() {
				var a = this.checkboxes;
				for (var i = 0; i < a.length; i++) {
					a[i].setValue(false);
				}
			},
			isNoRelativePeriods: function() {
				var a = this.checkboxes;
				for (var i = 0; i < a.length; i++) {
					if (a[i].getValue()) {
						return false;
					}
				}
				return true;
			},
			items: [
				{
					xtype: 'panel',
					layout: 'column',
					bodyStyle: 'border-style:none',
					style: 'margin-top:0px',
					items: [
						{
							xtype: 'combobox',
							cls: 'ns-combo',
							style: 'margin-bottom:2px',
							width: ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding - 62 - 62 - 4,
							valueField: 'id',
							displayField: 'name',
							emptyText: NS.i18n.select_period_type,
							editable: false,
							queryMode: 'remote',
							store: periodTypeStore,
							periodOffset: 0,
							listeners: {
								select: function() {
									var nsype = new PeriodType(),
										periodType = this.getValue();

									var periods = nsype.get(periodType).generatePeriods({
										offset: this.periodOffset,
										filterFuturePeriods: true,
										reversePeriods: true
									});

									fixedPeriodAvailableStore.setIndex(periods);
									fixedPeriodAvailableStore.loadData(periods);
									ns.core.web.multiSelect.filterAvailable(fixedPeriodAvailable, fixedPeriodSelected);
								}
							}
						},
						{
							xtype: 'button',
							text: NS.i18n.prev_year,
							style: 'margin-left:2px; border-radius:2px',
							height: 24,
							handler: function() {
								var cb = this.up('panel').down('combobox');
								if (cb.getValue()) {
									cb.periodOffset--;
									cb.fireEvent('select');
								}
							}
						},
						{
							xtype: 'button',
							text: NS.i18n.next_year,
							style: 'margin-left:2px; border-radius:2px',
							height: 24,
							handler: function() {
								var cb = this.up('panel').down('combobox');
								if (cb.getValue() && cb.periodOffset < 0) {
									cb.periodOffset++;
									cb.fireEvent('select');
								}
							}
						}
					]
				},
				{
					xtype: 'panel',
					layout: 'column',
					bodyStyle: 'border-style:none; padding-bottom:2px',
					items: [
						fixedPeriodAvailable,
						fixedPeriodSelected
					]
				},
				relativePeriod
			],
			listeners: {
				added: function() {
					accordionPanels.push(this);
				},
				expand: function(p) {
					p.onExpand();
				}
			}
		};

		// organisation unit
		treePanel = Ext.create('Ext.tree.Panel', {
			cls: 'ns-tree',
			style: 'border-top: 1px solid #ddd; padding-top: 1px',
			width: ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding,
			displayField: 'name',
			rootVisible: false,
			autoScroll: true,
			multiSelect: true,
			rendered: false,
			reset: function() {
				var rootNode = this.getRootNode().findChild('id', ns.core.init.rootNodes[0].id);
				this.collapseAll();
				this.expandPath(rootNode.getPath());
				this.getSelectionModel().select(rootNode);
			},
			selectRootIf: function() {
				if (this.getSelectionModel().getSelection().length < 1) {
					var node = this.getRootNode().findChild('id', ns.core.init.rootNodes[0].id);
					if (this.rendered) {
						this.getSelectionModel().select(node);
					}
					return node;
				}
			},
			isPending: false,
			recordsToSelect: [],
			recordsToRestore: [],
			multipleSelectIf: function(map, doUpdate) {
				if (this.recordsToSelect.length === ns.core.support.prototype.object.getLength(map)) {
					this.getSelectionModel().select(this.recordsToSelect);
					this.recordsToSelect = [];
					this.isPending = false;

					if (doUpdate) {
						update();
					}
				}
			},
			multipleExpand: function(id, map, doUpdate) {
				var that = this,
					rootId = ns.core.conf.finals.root.id,
					path = map[id];

				if (path.substr(0, rootId.length + 1) !== ('/' + rootId)) {
					path = '/' + rootId + path;
				}

				that.expandPath(path, 'id', '/', function() {
					record = Ext.clone(that.getRootNode().findChild('id', id, true));
					that.recordsToSelect.push(record);
					that.multipleSelectIf(map, doUpdate);
				});
			},
            select: function(url, params) {
                if (!params) {
                    params = {};
                }
                Ext.Ajax.request({
                    url: url,
                    method: 'GET',
                    params: params,
                    scope: this,
                    success: function(r) {
                        var a = Ext.decode(r.responseText).organisationUnits;
                        this.numberOfRecords = a.length;
                        for (var i = 0; i < a.length; i++) {
                            this.multipleExpand(a[i].id, a[i].path);
                        }
                    }
                });
            },
			getParentGraphMap: function() {
				var selection = this.getSelectionModel().getSelection(),
					map = {};

				if (Ext.isArray(selection) && selection.length) {
					for (var i = 0, pathArray, key; i < selection.length; i++) {
						pathArray = selection[i].getPath().split('/');
						map[pathArray.pop()] = pathArray.join('/');
					}
				}

				return map;
			},
			selectGraphMap: function(map, update) {
				if (!ns.core.support.prototype.object.getLength(map)) {
					return;
				}

				this.isPending = true;

				for (var key in map) {
					if (map.hasOwnProperty(key)) {
						treePanel.multipleExpand(key, map, update);
					}
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
					url: ns.core.init.contextPath + '/api/organisationUnits',
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
					id: ns.core.conf.finals.root.id,
					expanded: true,
					children: ns.core.init.rootNodes
				},
				listeners: {
					load: function(store, node, records) {
						Ext.Array.each(records, function(record) {
							record.set('leaf', !record.raw.hasChildren);
						});
					}
				}
			}),
			xable: function(values) {
				for (var i = 0; i < values.length; i++) {
					if (!!values[i]) {
						this.disable();
						return;
					}
				}

				this.enable();
			},
			listeners: {
				beforeitemexpand: function() {
					if (!treePanel.isPending) {
						treePanel.recordsToRestore = treePanel.getSelectionModel().getSelection();
					}
				},
				itemexpand: function() {
					if (!treePanel.isPending && treePanel.recordsToRestore.length) {
						treePanel.getSelectionModel().select(treePanel.recordsToRestore);
						treePanel.recordsToRestore = [];
					}
				},
				render: function() {
					this.rendered = true;
				},
				afterrender: function() {
					this.getSelectionModel().select(0);
				},
				itemcontextmenu: function(v, r, h, i, e) {
					v.getSelectionModel().select(r, false);

					if (v.menu) {
						v.menu.destroy();
					}
					v.menu = Ext.create('Ext.menu.Menu', {
						id: 'treepanel-contextmenu',
						showSeparator: false,
						shadow: false
					});
					if (!r.data.leaf) {
						v.menu.add({
							id: 'treepanel-contextmenu-item',
							text: NS.i18n.select_all_children,
							icon: 'images/node-select-child.png',
							handler: function() {
								r.expand(false, function() {
									v.getSelectionModel().select(r.childNodes, true);
									v.getSelectionModel().deselect(r);
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
		});

		userOrganisationUnit = Ext.create('Ext.form.field.Checkbox', {
			columnWidth: 0.28,
			style: 'padding-top:2px; padding-left:3px; margin-bottom:0',
			boxLabel: NS.i18n.user_organisation_unit,
			labelWidth: ns.core.conf.layout.form_label_width,
			handler: function(chb, checked) {
				treePanel.xable([checked, userOrganisationUnitChildren.getValue(), userOrganisationUnitGrandChildren.getValue()]);
			}
		});

		userOrganisationUnitChildren = Ext.create('Ext.form.field.Checkbox', {
			columnWidth: 0.31,
			style: 'padding-top:2px; margin-bottom:0',
			boxLabel: NS.i18n.user_organisation_unit_children,
			labelWidth: ns.core.conf.layout.form_label_width,
			handler: function(chb, checked) {
				treePanel.xable([checked, userOrganisationUnit.getValue(), userOrganisationUnitGrandChildren.getValue()]);
			}
		});

		userOrganisationUnitGrandChildren = Ext.create('Ext.form.field.Checkbox', {
			columnWidth: 0.41,
			style: 'padding-top:2px; margin-bottom:0',
			boxLabel: NS.i18n.user_organisation_unit_grandchildren,
			labelWidth: ns.core.conf.layout.form_label_width,
			handler: function(chb, checked) {
				treePanel.xable([checked, userOrganisationUnit.getValue(), userOrganisationUnitChildren.getValue()]);
			}
		});

		organisationUnitLevel = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			multiSelect: true,
			style: 'margin-bottom:0',
			width: ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding - 38,
			valueField: 'level',
			displayField: 'name',
			emptyText: NS.i18n.select_organisation_unit_levels,
			editable: false,
			hidden: true,
			store: organisationUnitLevelStore
		});

		organisationUnitGroup = Ext.create('Ext.form.field.ComboBox', {
			cls: 'ns-combo',
			multiSelect: true,
			style: 'margin-bottom:0',
			width: ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding - 38,
			valueField: 'id',
			displayField: 'name',
			emptyText: NS.i18n.select_organisation_unit_groups,
			editable: false,
			hidden: true,
			store: organisationUnitGroupStore
		});

		toolMenu = Ext.create('Ext.menu.Menu', {
			shadow: false,
			showSeparator: false,
			menuValue: 'orgunit',
			clickHandler: function(param) {
				if (!param) {
					return;
				}

				var items = this.items.items;
				this.menuValue = param;

				// Menu item icon cls
				for (var i = 0; i < items.length; i++) {
					if (items[i].setIconCls) {
						if (items[i].param === param) {
							items[i].setIconCls('ns-menu-item-selected');
						}
						else {
							items[i].setIconCls('ns-menu-item-unselected');
						}
					}
				}

				// Gui
				if (param === 'orgunit') {
					userOrganisationUnit.show();
					userOrganisationUnitChildren.show();
					userOrganisationUnitGrandChildren.show();
					organisationUnitLevel.hide();
					organisationUnitGroup.hide();

					if (userOrganisationUnit.getValue() || userOrganisationUnitChildren.getValue()) {
						treePanel.disable();
					}
				}
				else if (param === 'level') {
					userOrganisationUnit.hide();
					userOrganisationUnitChildren.hide();
					userOrganisationUnitGrandChildren.hide();
					organisationUnitLevel.show();
					organisationUnitGroup.hide();
					treePanel.enable();
				}
				else if (param === 'group') {
					userOrganisationUnit.hide();
					userOrganisationUnitChildren.hide();
					userOrganisationUnitGrandChildren.hide();
					organisationUnitLevel.hide();
					organisationUnitGroup.show();
					treePanel.enable();
				}
			},
			items: [
				{
					xtype: 'label',
					text: 'Selection mode',
					style: 'padding:7px 5px 5px 7px; font-weight:bold; border:0 none'
				},
				{
					text: NS.i18n.select_organisation_units + '&nbsp;&nbsp;',
					param: 'orgunit',
					iconCls: 'ns-menu-item-selected'
				},
				{
					text: 'Select levels' + '&nbsp;&nbsp;',
					param: 'level',
					iconCls: 'ns-menu-item-unselected'
				},
				{
					text: 'Select groups' + '&nbsp;&nbsp;',
					param: 'group',
					iconCls: 'ns-menu-item-unselected'
				}
			],
			listeners: {
				afterrender: function() {
					this.getEl().addCls('ns-btn-menu');
				},
				click: function(menu, item) {
					this.clickHandler(item.param);
				}
			}
		});

		tool = Ext.create('Ext.button.Button', {
			cls: 'ns-button-organisationunitselection',
			iconCls: 'ns-button-icon-gear',
			width: 36,
			height: 24,
			menu: toolMenu
		});

		toolPanel = Ext.create('Ext.panel.Panel', {
			width: 36,
			bodyStyle: 'border:0 none; text-align:right',
			style: 'margin-right:2px',
			items: tool
		});

		organisationUnit = {
			xtype: 'panel',
			title: '<div class="ns-panel-title-organisationunit">' + NS.i18n.organisation_units + '</div>',
			bodyStyle: 'padding:2px',
			hideCollapseTool: true,
			collapsed: false,
			getDimension: function() {
				var r = treePanel.getSelectionModel().getSelection(),
					config = {
						dimension: ns.core.conf.finals.dimension.organisationUnit.objectName,
						items: []
					};

				if (toolMenu.menuValue === 'orgunit') {
					if (userOrganisationUnit.getValue() || userOrganisationUnitChildren.getValue() || userOrganisationUnitGrandChildren.getValue()) {
						if (userOrganisationUnit.getValue()) {
							config.items.push({
								id: 'USER_ORGUNIT',
								name: ''
							});
						}
						if (userOrganisationUnitChildren.getValue()) {
							config.items.push({
								id: 'USER_ORGUNIT_CHILDREN',
								name: ''
							});
						}
						if (userOrganisationUnitGrandChildren.getValue()) {
							config.items.push({
								id: 'USER_ORGUNIT_GRANDCHILDREN',
								name: ''
							});
						}
					}
					else {
						for (var i = 0; i < r.length; i++) {
							config.items.push({id: r[i].data.id});
						}
					}
				}
				else if (toolMenu.menuValue === 'level') {
					var levels = organisationUnitLevel.getValue();

					for (var i = 0; i < levels.length; i++) {
						config.items.push({
							id: 'LEVEL-' + levels[i],
							name: ''
						});
					}

					for (var i = 0; i < r.length; i++) {
						config.items.push({
							id: r[i].data.id,
							name: ''
						});
					}
				}
				else if (toolMenu.menuValue === 'group') {
					var groupIds = organisationUnitGroup.getValue();

					for (var i = 0; i < groupIds.length; i++) {
						config.items.push({
							id: 'OU_GROUP-' + groupIds[i],
							name: ''
						});
					}

					for (var i = 0; i < r.length; i++) {
						config.items.push({
							id: r[i].data.id,
							name: ''
						});
					}
				}

				return config.items.length ? config : null;
			},
            onExpand: function() {
                var h = ns.app.westRegion.hasScrollbar ?
                    ns.core.conf.layout.west_scrollbarheight_accordion_organisationunit : ns.core.conf.layout.west_maxheight_accordion_organisationunit;
                accordion.setThisHeight(h);
                treePanel.setHeight(this.getHeight() - ns.core.conf.layout.west_fill_accordion_organisationunit);
            },
            items: [
                {
                    layout: 'column',
                    bodyStyle: 'border:0 none',
                    style: 'padding-bottom:2px',
                    items: [
                        toolPanel,
                        {
                            width: ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding - 38,
                            layout: 'column',
                            bodyStyle: 'border:0 none',
                            items: [
                                userOrganisationUnit,
                                userOrganisationUnitChildren,
                                userOrganisationUnitGrandChildren,
                                organisationUnitLevel,
                                organisationUnitGroup
                            ]
                        }
                    ]
                },
                treePanel
            ],
            listeners: {
                added: function() {
                    accordionPanels.push(this);
                },
                expand: function(p) {
                    p.onExpand();
                }
            }
        };

		// dimensions
		getDimensionPanels = function(dimensions, iconCls) {
			var	getAvailableStore,
				getSelectedStore,

				createPanel,
				getPanels;

			getAvailableStore = function(dimension) {
				return Ext.create('Ext.data.Store', {
					fields: ['id', 'name'],
					proxy: {
						type: 'ajax',
						url: ns.core.init.contextPath + '/api/dimensions/' + dimension.id + '.json',
						reader: {
							type: 'json',
							root: 'items'
						}
					},
					isLoaded: false,
					storage: {},
					sortStore: function() {
						this.sort('name', 'ASC');
					},
					reset: function() {
						if (this.isLoaded) {
							this.removeAll();
							ns.core.web.storage.internal.load(this);
							this.sortStore();
						}
					},
					listeners: {
						load: function(s) {
							s.isLoaded = true;
							ns.core.web.storage.internal.add(s);
						}
					}
				});
			};

			getSelectedStore = function() {
				return Ext.create('Ext.data.Store', {
					fields: ['id', 'name'],
					data: []
				});
			};

			createPanel = function(dimension) {
				var getAvailable,
					getSelected,

					availableStore,
					selectedStore,
					available,
					selected,

					panel;

				getAvailable = function(availableStore) {
					return Ext.create('Ext.ux.form.MultiSelect', {
						cls: 'ns-toolbar-multiselect-left',
						width: (ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding) / 2,
						valueField: 'id',
						displayField: 'name',
						store: availableStore,
						tbar: [
							{
								xtype: 'label',
								text: NS.i18n.available,
								cls: 'ns-toolbar-multiselect-left-label'
							},
							'->',
							{
								xtype: 'button',
								icon: 'images/arrowright.png',
								width: 22,
								handler: function() {
									ns.core.web.multiSelect.select(available, selected);
								}
							},
							{
								xtype: 'button',
								icon: 'images/arrowrightdouble.png',
								width: 22,
								handler: function() {
									ns.core.web.multiSelect.selectAll(available, selected);
								}
							}
						],
						listeners: {
							afterrender: function() {
								this.boundList.on('itemdblclick', function() {
									ns.core.web.multiSelect.select(available, selected);
								}, this);
							}
						}
					});
				};

				getSelected = function(selectedStore) {
					return Ext.create('Ext.ux.form.MultiSelect', {
						cls: 'ns-toolbar-multiselect-right',
						width: (ns.core.conf.layout.west_fieldset_width - ns.core.conf.layout.west_width_padding) / 2,
						valueField: 'id',
						displayField: 'name',
						ddReorder: true,
						store: selectedStore,
						tbar: [
							{
								xtype: 'button',
								icon: 'images/arrowleftdouble.png',
								width: 22,
								handler: function() {
									ns.core.web.multiSelect.unselectAll(available, selected);
								}
							},
							{
								xtype: 'button',
								icon: 'images/arrowleft.png',
								width: 22,
								handler: function() {
									ns.core.web.multiSelect.unselect(available, selected);
								}
							},
							'->',
							{
								xtype: 'label',
								text: NS.i18n.selected,
								cls: 'ns-toolbar-multiselect-right-label'
							}
						],
						listeners: {
							afterrender: function() {
								this.boundList.on('itemdblclick', function() {
									ns.core.web.multiSelect.unselect(available, selected);
								}, this);
							}
						}
					});
				};

				availableStore = getAvailableStore(dimension);
				selectedStore = getSelectedStore();

				dimensionIdAvailableStoreMap[dimension.id] = availableStore;
				dimensionIdSelectedStoreMap[dimension.id] = selectedStore;

				available = getAvailable(availableStore);
				selected = getSelected(selectedStore);

				availableStore.on('load', function() {
					ns.core.web.multiSelect.filterAvailable(available, selected);
				});

				panel = {
					xtype: 'panel',
					title: '<div class="' + iconCls + '">' + dimension.name + '</div>',
					hideCollapseTool: true,
					availableStore: availableStore,
					selectedStore: selectedStore,
					getDimension: function() {
						var config = {
							dimension: dimension.id,
							items: []
						};

						selectedStore.each( function(r) {
							config.items.push({id: r.data.id});
						});

						return config.items.length ? config : null;
					},
					onExpand: function() {
						if (!availableStore.isLoaded) {
							availableStore.load();
						}

						var h = ns.app.westRegion.hasScrollbar ?
							ns.core.conf.layout.west_scrollbarheight_accordion_group : ns.core.conf.layout.west_maxheight_accordion_group;
						accordion.setThisHeight(h);
						ns.core.web.multiSelect.setHeight(
							[available, selected],
							this,
							ns.core.conf.layout.west_fill_accordion_dataset
						);
					},
					items: [
						{
							xtype: 'panel',
							layout: 'column',
							bodyStyle: 'border-style:none',
							items: [
								available,
								selected
							]
						}
					],
					listeners: {
						added: function() {
							accordionPanels.push(this);
						},
						expand: function(p) {
							p.onExpand();
						}
					}
				};

				return panel;
			};

			getPanels = function() {
				var panels = [];

				for (var i = 0, panel; i < dimensions.length; i++) {
					panel = createPanel(dimensions[i]);

					panels.push(panel);
				}

				return panels;
			};

			return getPanels();
		};

		// viewport
		update = function() {
			var config = ns.core.web.pivot.getLayoutConfig(),
				layout = ns.core.api.layout.Layout(config);

			if (!layout) {
				return;
			}

			ns.core.web.pivot.getData(layout, false);
		};

		accordionBody = Ext.create('Ext.panel.Panel', {
			layout: 'accordion',
			activeOnTop: true,
			cls: 'ns-accordion',
			bodyStyle: 'border:0 none; margin-bottom:2px',
			height: 700,
			items: function() {
				var panels = [
					indicator,
					dataElement,
					dataSet,
					period,
					organisationUnit
				],
				dims = Ext.clone(ns.core.init.dimensions);

				panels = panels.concat(getDimensionPanels(dims, 'ns-panel-title-dimension'));

				last = panels[panels.length - 1];
				last.cls = 'ns-accordion-last';

				return panels;
			}()
		});

		accordion = Ext.create('Ext.panel.Panel', {
			bodyStyle: 'border-style:none; padding:2px; padding-bottom:0; overflow-y:scroll;',
			items: accordionBody,
			panels: accordionPanels,
			setThisHeight: function(mx) {
				var panelHeight = this.panels.length * 28,
					height;

				if (westRegion.hasScrollbar) {
					height = panelHeight + mx;
					this.setHeight(viewport.getHeight() - 2);
					accordionBody.setHeight(height - 2);
				}
				else {
					height = westRegion.getHeight() - ns.core.conf.layout.west_fill;
					mx += panelHeight;
					accordion.setHeight((height > mx ? mx : height) - 2);
					accordionBody.setHeight((height > mx ? mx : height) - 2);
				}
			},
			getExpandedPanel: function() {
				for (var i = 0, panel; i < this.panels.length; i++) {
					if (!this.panels[i].collapsed) {
						return this.panels[i];
					}
				}

				return null;
			},
			getFirstPanel: function() {
				return this.panels[0];
			},
			listeners: {
				added: function() {
					ns.app.accordion = this;
				}
			}
		});

		westRegion = Ext.create('Ext.panel.Panel', {
			region: 'west',
			preventHeader: true,
			collapsible: true,
			collapseMode: 'mini',
			width: function() {
				if (Ext.isWebKit) {
					return ns.core.conf.layout.west_width + 8;
				}
				else {
					if (Ext.isLinux && Ext.isGecko) {
						return ns.core.conf.layout.west_width + 13;
					}
					return ns.core.conf.layout.west_width + 17;
				}
			}(),
			items: accordion,
			listeners: {
				added: function() {
					ns.app.westRegion = this;
				}
			}
		});

		layoutButton = Ext.create('Ext.button.Button', {
			text: 'Layout',
			menu: {},
			handler: function() {
				if (!ns.app.layoutWindow) {
					ns.app.layoutWindow = LayoutWindow();
				}

				ns.app.layoutWindow.show();
			},
			listeners: {
				added: function() {
					ns.app.layoutButton = this;
				}
			}
		});

		optionsButton = Ext.create('Ext.button.Button', {
			text: NS.i18n.options,
			menu: {},
			handler: function() {
				if (!ns.app.optionsWindow) {
					ns.app.optionsWindow = OptionsWindow();
				}

				ns.app.optionsWindow.show();
			},
			listeners: {
				added: function() {
					ns.app.optionsButton = this;
				}
			}
		});

		favoriteButton = Ext.create('Ext.button.Button', {
			text: NS.i18n.favorites,
			menu: {},
			handler: function() {
				if (ns.app.favoriteWindow) {
					ns.app.favoriteWindow.destroy();
					ns.app.favoriteWindow = null;
				}

				ns.app.favoriteWindow = FavoriteWindow();
				ns.app.favoriteWindow.show();
			},
			listeners: {
				added: function() {
					ns.app.favoriteButton = this;
				}
			}
		});

		getParamString = function() {
			var paramString = ns.core.web.analytics.getParamString(ns.core.service.layout.getExtendedLayout(ns.app.layout));

			if (ns.app.layout.showHierarchy) {
				paramString += '&showHierarchy=true';
			}

			return paramString;
		};

		openTableLayoutTab = function(type, isNewTab) {
			if (ns.core.init.contextPath && ns.app.paramString) {
				var colDimNames = Ext.clone(ns.app.xLayout.columnDimensionNames),
					colObjNames = ns.app.xLayout.columnObjectNames,
					rowDimNames = Ext.clone(ns.app.xLayout.rowDimensionNames),
					rowObjNames = ns.app.xLayout.rowObjectNames,
					dc = ns.core.conf.finals.dimension.operand.objectName,
					co = ns.core.conf.finals.dimension.category.dimensionName,
					columnNames = Ext.Array.clean([].concat(colDimNames, (Ext.Array.contains(colObjNames, dc) ? co : []))),
					rowNames = Ext.Array.clean([].concat(rowDimNames, (Ext.Array.contains(rowObjNames, dc) ? co : []))),
					url = '';

				url += ns.core.init.contextPath + '/api/analytics.' + type + getParamString();
				url += '&tableLayout=true';
				url += '&columns=' + columnNames.join(';');
				url += '&rows=' + rowNames.join(';');
				url += ns.app.layout.hideEmptyRows ? '&hideEmptyRows=true' : '';

				window.open(url, isNewTab ? '_blank' : '_top');
			}
		};

		downloadButton = Ext.create('Ext.button.Button', {
			text: 'Download',
			disabled: true,
			menu: {
				cls: 'ns-menu',
				shadow: false,
				showSeparator: false,
				items: [
					{
						xtype: 'label',
						text: NS.i18n.table_layout,
						style: 'padding:7px 5px 5px 7px; font-weight:bold; border:0 none'
					},
					{
						text: 'Microsoft Excel (.xls)',
						iconCls: 'ns-menu-item-tablelayout',
						handler: function() {
							openTableLayoutTab('xls');
						}
					},
					{
						text: 'CSV (.csv)',
						iconCls: 'ns-menu-item-tablelayout',
						handler: function() {
							openTableLayoutTab('csv');
						}
					},
					{
						text: 'HTML (.html)',
						iconCls: 'ns-menu-item-tablelayout',
						handler: function() {
							openTableLayoutTab('html', true);
						}
					},
					{
						xtype: 'label',
						text: NS.i18n.plain_data_sources,
						style: 'padding:7px 5px 5px 7px; font-weight:bold'
					},
					{
						text: 'JSON',
						iconCls: 'ns-menu-item-datasource',
						handler: function() {
							if (ns.core.init.contextPath && ns.app.paramString) {
								window.open(ns.core.init.contextPath + '/api/analytics.json' + getParamString(), '_blank');
							}
						}
					},
					{
						text: 'XML',
						iconCls: 'ns-menu-item-datasource',
						handler: function() {
							if (ns.core.init.contextPath && ns.app.paramString) {
								window.open(ns.core.init.contextPath + '/api/analytics.xml' + getParamString(), '_blank');
							}
						}
					},
					{
						text: 'Microsoft Excel',
						iconCls: 'ns-menu-item-datasource',
						handler: function() {
							if (ns.core.init.contextPath && ns.app.paramString) {
								window.location.href = ns.core.init.contextPath + '/api/analytics.xls' + getParamString();
							}
						}
					},
					{
						text: 'CSV',
						iconCls: 'ns-menu-item-datasource',
						handler: function() {
							if (ns.core.init.contextPath && ns.app.paramString) {
								window.location.href = ns.core.init.contextPath + '/api/analytics.csv' + getParamString();
							}
						}
					},
					{
						text: 'JRXML',
						iconCls: 'ns-menu-item-datasource',
						handler: function() {
							if (ns.core.init.contextPath && ns.app.paramString) {
								window.open(ns.core.init.contextPath + '/api/analytics.jrxml' + getParamString(), '_blank');
							}
						}
					}
				],
				listeners: {
					added: function() {
						ns.app.downloadButton = this;
					},
					afterrender: function() {
						this.getEl().addCls('ns-toolbar-btn-menu');
					}
				}
			}
		});

		interpretationItem = Ext.create('Ext.menu.Item', {
			text: 'Write interpretation' + '&nbsp;&nbsp;',
			iconCls: 'ns-menu-item-tablelayout',
			disabled: true,
			xable: function() {
				if (ns.app.layout.id) {
					this.enable();
				}
				else {
					this.disable();
				}
			},
			handler: function() {
				if (ns.app.interpretationWindow) {
					ns.app.interpretationWindow.destroy();
					ns.app.interpretationWindow = null;
				}

				ns.app.interpretationWindow = InterpretationWindow();
				ns.app.interpretationWindow.show();
			}
		});

		pluginItem = Ext.create('Ext.menu.Item', {
			text: 'Embed as plugin' + '&nbsp;&nbsp;',
			iconCls: 'ns-menu-item-datasource',
			disabled: true,
			xable: function() {
				if (ns.app.layout) {
					this.enable();
				}
				else {
					this.disable();
				}
			},
			handler: function() {
				var textArea,
					window,
					text = '';

				text += '<html>\n<head>\n';
				text += '<link rel="stylesheet" href="http://dhis2-cdn.org/v214/ext/resources/css/ext-plugin-gray.css" />\n';
				text += '<script src="http://dhis2-cdn.org/v214/ext/ext-all.js"></script>\n';
				text += '<script src="http://dhis2-cdn.org/v214/plugin/table.js"></script>\n';
				text += '</head>\n\n<body>\n';
				text += '<div id="table1"></div>\n\n';
				text += '<script>\n\n';
				text += 'DHIS.getTable(' + JSON.stringify(ns.core.service.layout.layout2plugin(ns.app.layout, 'table1'), null, 2) + ');\n\n';
				text += '</script>\n\n';
				text += '</body>\n</html>';

				textArea = Ext.create('Ext.form.field.TextArea', {
					width: 700,
					height: 400,
					readOnly: true,
					cls: 'ns-textarea monospaced',
					value: text
				});

				window = Ext.create('Ext.window.Window', {
					title: 'Plugin configuration',
					layout: 'fit',
					modal: true,
					resizable: false,
					items: textArea,
					destroyOnBlur: true,
					bbar: [
						'->',
						{
							text: 'Select',
							handler: function() {
								textArea.selectText();
							}
						}
					],
					listeners: {
						show: function(w) {
							ns.core.web.window.setAnchorPosition(w, ns.app.shareButton);

							document.body.oncontextmenu = true;

							if (!w.hasDestroyOnBlurHandler) {
								ns.core.web.window.addDestroyOnBlurHandler(w);
							}
						},
						hide: function() {
							document.body.oncontextmenu = function(){return false;};
						}
					}
				});

				window.show();
			}
		});

		shareButton = Ext.create('Ext.button.Button', {
			text: NS.i18n.share,
			xableItems: function() {
				interpretationItem.xable();
				pluginItem.xable();
			},
			menu: {
				cls: 'ns-menu',
				shadow: false,
				showSeparator: false,
				items: [
					interpretationItem,
					pluginItem
				],
				listeners: {
					afterrender: function() {
						this.getEl().addCls('ns-toolbar-btn-menu');
					},
					show: function() {
						shareButton.xableItems();
					}
				}
			},
			listeners: {
				added: function() {
					ns.app.shareButton = this;
				}
			}
		});

		defaultButton = Ext.create('Ext.button.Button', {
			text: NS.i18n.table,
			iconCls: 'ns-button-icon-table',
			toggleGroup: 'module',
			pressed: true,
			handler: function() {
				if (!this.pressed) {
					this.toggle();
				}
			}
		});

		centerRegion = Ext.create('Ext.panel.Panel', {
			region: 'center',
			bodyStyle: 'padding:1px',
			autoScroll: true,
			tbar: {
				defaults: {
					height: 26
				},
				items: [
					{
						text: '<<<',
						handler: function(b) {
							var text = b.getText();
							text = text === '<<<' ? '>>>' : '<<<';
							b.setText(text);

							westRegion.toggleCollapse();
						}
					},
					{
						text: '<b>' + NS.i18n.update + '</b>',
						handler: function() {
							update();
						}
					},
					layoutButton,
					optionsButton,
					{
						xtype: 'tbseparator',
						height: 18,
						style: 'border-color:transparent; border-right-color:#d1d1d1; margin-right:4px',
					},
					favoriteButton,
					downloadButton,
					shareButton,
					'->',
					defaultButton,
					{
						text: NS.i18n.chart,
						iconCls: 'ns-button-icon-chart',
						toggleGroup: 'module',
						menu: {},
						handler: function(b) {
							b.menu = Ext.create('Ext.menu.Menu', {
								closeAction: 'destroy',
								shadow: false,
								showSeparator: false,
								items: [
                                    {
                                        text: 'Open this table as chart' + '&nbsp;&nbsp;', //i18n
                                        cls: 'ns-menu-item-noicon',
                                        disabled: !(NS.isSessionStorage && ns.app.layout),
                                        handler: function() {
                                            if (NS.isSessionStorage) {
                                                ns.app.layout.parentGraphMap = treePanel.getParentGraphMap();
                                                ns.core.web.storage.session.set(ns.app.layout, 'analytical', ns.core.init.contextPath + '/dhis-web-visualizer/app/index.html?s=analytical');
                                            }
                                        }
                                    },
                                    {
                                        text: 'Open last chart' + '&nbsp;&nbsp;', //i18n
                                        cls: 'ns-menu-item-noicon',
                                        disabled: !(NS.isSessionStorage && JSON.parse(sessionStorage.getItem('dhis2')) && JSON.parse(sessionStorage.getItem('dhis2'))['chart']),
                                        handler: function() {
                                            window.location.href = ns.core.init.contextPath + '/dhis-web-visualizer/app/index.html?s=chart';
                                        }
                                    },
                                    '-',
									{
										text: 'Go to charts' + '&nbsp;&nbsp;', //i18n
										cls: 'ns-menu-item-noicon',
										handler: function() {
											window.location.href = ns.core.init.contextPath + '/dhis-web-visualizer/app/index.html';
										}
									}
								],
								listeners: {
									show: function() {
										ns.core.web.window.setAnchorPosition(b.menu, b);
									},
									hide: function() {
										b.menu.destroy();
										defaultButton.toggle();
									},
									destroy: function(m) {
										b.menu = null;
									}
								}
							});

							b.menu.show();
						}
					},
					{
						text: NS.i18n.map,
						iconCls: 'ns-button-icon-map',
						toggleGroup: 'module',
						menu: {},
						handler: function(b) {
							b.menu = Ext.create('Ext.menu.Menu', {
								closeAction: 'destroy',
								shadow: false,
								showSeparator: false,
								items: [
                                    {
                                        text: 'Open this table as map' + '&nbsp;&nbsp;', //i18n
                                        cls: 'ns-menu-item-noicon',
                                        disabled: !(NS.isSessionStorage && ns.app.layout),
                                        handler: function() {
                                            if (NS.isSessionStorage) {
                                                ns.app.layout.parentGraphMap = treePanel.getParentGraphMap();
                                                ns.core.web.storage.session.set(ns.app.layout, 'analytical', ns.core.init.contextPath + '/dhis-web-mapping/app/index.html?s=analytical');
                                            }
                                        }
                                    },
                                    {
                                        text: 'Open last map' + '&nbsp;&nbsp;', //i18n
                                        cls: 'ns-menu-item-noicon',
                                        disabled: !(NS.isSessionStorage && JSON.parse(sessionStorage.getItem('dhis2')) && JSON.parse(sessionStorage.getItem('dhis2'))['map']),
                                        handler: function() {
                                            window.location.href = ns.core.init.contextPath + '/dhis-web-mapping/app/index.html?s=map';
                                        }
                                    },
                                    '-',
									{
										text: 'Go to maps' + '&nbsp;&nbsp;', //i18n
										cls: 'ns-menu-item-noicon',
										handler: function() {
											window.location.href = ns.core.init.contextPath + '/dhis-web-mapping/app/index.html';
										}
									}

								],
								listeners: {
									show: function() {
										ns.core.web.window.setAnchorPosition(b.menu, b);
									},
									hide: function() {
										b.menu.destroy();
										defaultButton.toggle();
									},
									destroy: function(m) {
										b.menu = null;
									}
								}
							});

							b.menu.show();
						}
					},
					{
						xtype: 'tbseparator',
						height: 18,
						style: 'border-color:transparent; border-right-color:#d1d1d1; margin-right:4px',
					},
					{
						xtype: 'button',
						text: NS.i18n.home,
						handler: function() {
							window.location.href = ns.core.init.contextPath + '/dhis-web-commons-about/redirect.action';
						}
					}
				]
			},
			listeners: {
				added: function() {
					ns.app.centerRegion = this;
				},
				afterrender: function(p) {
					var liStyle = 'padding:3px 10px; color:#333',
						html = '';

					html += '<div style="padding:20px">';
					html += '<div style="font-size:14px; padding-bottom:8px">' + NS.i18n.example1 + '</div>';
					html += '<div style="' + liStyle + '">- ' + NS.i18n.example2 + '</div>';
					html += '<div style="' + liStyle + '">- ' + NS.i18n.example3 + '</div>';
					html += '<div style="' + liStyle + '">- ' + NS.i18n.example4 + '</div>';
					html += '<div style="font-size:14px; padding-top:20px; padding-bottom:8px">' + NS.i18n.example5 + '</div>';
					html += '<div style="' + liStyle + '">- ' + NS.i18n.example6 + '</div>';
					html += '<div style="' + liStyle + '">- ' + NS.i18n.example7 + '</div>';
					html += '<div style="' + liStyle + '">- ' + NS.i18n.example8 + '</div>';
					html += '</div>';

					p.update(html);
				}
			}
		});

		setGui = function(layout, xLayout, updateGui) {
			var dimensions = Ext.Array.clean([].concat(layout.columns || [], layout.rows || [], layout.filters || [])),
				dimMap = ns.core.service.layout.getObjectNameDimensionMapFromDimensionArray(dimensions),
				recMap = ns.core.service.layout.getObjectNameDimensionItemsMapFromDimensionArray(dimensions),
				graphMap = layout.parentGraphMap,
				objectName,
				periodRecords,
				fixedPeriodRecords = [],
				dimNames = [],
				isOu = false,
				isOuc = false,
				isOugc = false,
				levels = [],
				groups = [],
				orgunits = [];

			// State
			downloadButton.enable();

			if (layout.id) {
				shareButton.enable();
			}

			// Set gui
			if (!updateGui) {
				return;
			}

			// Indicators
			indicatorSelectedStore.removeAll();
			objectName = dimConf.indicator.objectName;
			if (dimMap[objectName]) {
				indicatorSelectedStore.add(Ext.clone(recMap[objectName]));
				ns.core.web.multiSelect.filterAvailable({store: indicatorAvailableStore}, {store: indicatorSelectedStore});
			}

			// Data elements
			dataElementSelectedStore.removeAll();
			objectName = dimConf.dataElement.objectName;
			if (dimMap[objectName]) {
				dataElementSelectedStore.add(Ext.clone(recMap[objectName]));
				ns.core.web.multiSelect.filterAvailable({store: dataElementAvailableStore}, {store: dataElementSelectedStore});
				dataElementDetailLevel.setValue(objectName);
			}

			// Operands
			objectName = dimConf.operand.objectName;
			if (dimMap[objectName]) {
				dataElementSelectedStore.add(Ext.clone(recMap[objectName]));
				ns.core.web.multiSelect.filterAvailable({store: dataElementAvailableStore}, {store: dataElementSelectedStore});
				dataElementDetailLevel.setValue(objectName);
			}

			// Data sets
			dataSetSelectedStore.removeAll();
			objectName = dimConf.dataSet.objectName;
			if (dimMap[objectName]) {
				dataSetSelectedStore.add(Ext.clone(recMap[objectName]));
				ns.core.web.multiSelect.filterAvailable({store: dataSetAvailableStore}, {store: dataSetSelectedStore});
			}

			// Periods
			fixedPeriodSelectedStore.removeAll();
			period.resetRelativePeriods();
			periodRecords = recMap[dimConf.period.objectName] || [];
			for (var i = 0, periodRecord, checkbox; i < periodRecords.length; i++) {
				periodRecord = periodRecords[i];
				checkbox = relativePeriod.valueComponentMap[periodRecord.id];
				if (checkbox) {
					checkbox.setValue(true);
				}
				else {
					fixedPeriodRecords.push(periodRecord);
				}
			}
			fixedPeriodSelectedStore.add(fixedPeriodRecords);
			ns.core.web.multiSelect.filterAvailable({store: fixedPeriodAvailableStore}, {store: fixedPeriodSelectedStore});

			// Group sets
			for (var key in dimensionIdSelectedStoreMap) {
				if (dimensionIdSelectedStoreMap.hasOwnProperty(key)) {
					var a = dimensionIdAvailableStoreMap[key],
						s = dimensionIdSelectedStoreMap[key];

					if (s.getCount() > 0) {
						a.reset();
						s.removeAll();
					}

					if (recMap[key]) {
						s.add(recMap[key]);
						ns.core.web.multiSelect.filterAvailable({store: a}, {store: s});
					}
				}
			}

			// Layout
			ns.app.stores.dimension.reset(true);
			ns.app.stores.col.removeAll();
			ns.app.stores.row.removeAll();
			ns.app.stores.filter.removeAll();

			if (layout.columns) {
				dimNames = [];

				for (var i = 0, dim; i < layout.columns.length; i++) {
					dim = dimConf.objectNameMap[layout.columns[i].dimension];

					if (!Ext.Array.contains(dimNames, dim.dimensionName)) {
						ns.app.stores.col.add({
							id: dim.dimensionName,
							name: dimConf.objectNameMap[dim.dimensionName].name
						});

						dimNames.push(dim.dimensionName);
					}

					ns.app.stores.dimension.remove(ns.app.stores.dimension.getById(dim.dimensionName));
				}
			}

			if (layout.rows) {
				dimNames = [];

				for (var i = 0, dim; i < layout.rows.length; i++) {
					dim = dimConf.objectNameMap[layout.rows[i].dimension];

					if (!Ext.Array.contains(dimNames, dim.dimensionName)) {
						ns.app.stores.row.add({
							id: dim.dimensionName,
							name: dimConf.objectNameMap[dim.dimensionName].name
						});

						dimNames.push(dim.dimensionName);
					}

					ns.app.stores.dimension.remove(ns.app.stores.dimension.getById(dim.dimensionName));
				}
			}

			if (layout.filters) {
				dimNames = [];

				for (var i = 0, dim; i < layout.filters.length; i++) {
					dim = dimConf.objectNameMap[layout.filters[i].dimension];

					if (!Ext.Array.contains(dimNames, dim.dimensionName)) {
						ns.app.stores.filter.add({
							id: dim.dimensionName,
							name: dimConf.objectNameMap[dim.dimensionName].name
						});

						dimNames.push(dim.dimensionName);
					}

					ns.app.stores.dimension.remove(ns.app.stores.dimension.getById(dim.dimensionName));
				}
			}

			// Options
			if (ns.app.optionsWindow) {
				ns.app.optionsWindow.setOptions(layout);
			}

			// Organisation units
			if (recMap[dimConf.organisationUnit.objectName]) {
				for (var i = 0, ouRecords = recMap[dimConf.organisationUnit.objectName]; i < ouRecords.length; i++) {
					if (ouRecords[i].id === 'USER_ORGUNIT') {
						isOu = true;
					}
					else if (ouRecords[i].id === 'USER_ORGUNIT_CHILDREN') {
						isOuc = true;
					}
					else if (ouRecords[i].id === 'USER_ORGUNIT_GRANDCHILDREN') {
						isOugc = true;
					}
					else if (ouRecords[i].id.substr(0,5) === 'LEVEL') {
						levels.push(parseInt(ouRecords[i].id.split('-')[1]));
					}
					else if (ouRecords[i].id.substr(0,8) === 'OU_GROUP') {
						groups.push(ouRecords[i].id.split('-')[1]);
					}
					else {
						orgunits.push(ouRecords[i].id);
					}
				}

				if (levels.length) {
					toolMenu.clickHandler('level');
					organisationUnitLevel.setValue(levels);
				}
				else if (groups.length) {
					toolMenu.clickHandler('group');
					organisationUnitGroup.setValue(groups);
				}
				else {
					toolMenu.clickHandler('orgunit');
					userOrganisationUnit.setValue(isOu);
					userOrganisationUnitChildren.setValue(isOuc);
					userOrganisationUnitGrandChildren.setValue(isOugc);
				}

				if (!(isOu || isOuc || isOugc)) {
					if (Ext.isObject(graphMap)) {
						treePanel.selectGraphMap(graphMap);
					}
				}
			}
			else {
				treePanel.reset();
			}
		};

		viewport = Ext.create('Ext.container.Viewport', {
			layout: 'border',
			period: period,
			treePanel: treePanel,
			setGui: setGui,
			items: [
				westRegion,
				centerRegion
			],
			listeners: {
				render: function() {
					ns.app.viewport = this;

					ns.app.layoutWindow = LayoutWindow();
					ns.app.layoutWindow.hide();
					ns.app.optionsWindow = OptionsWindow();
					ns.app.optionsWindow.hide();
				},
				afterrender: function() {

					// resize event handler
					westRegion.on('resize', function() {
						var panel = accordion.getExpandedPanel();

						if (panel) {
							panel.onExpand();
						}
					});

					// left gui
					var viewportHeight = westRegion.getHeight(),
						numberOfTabs = ns.core.init.dimensions.length + 5,
						tabHeight = 28,
						minPeriodHeight = 380;

					if (viewportHeight > numberOfTabs * tabHeight + minPeriodHeight) {
						if (!Ext.isIE) {
							accordion.setAutoScroll(false);
							westRegion.setWidth(ns.core.conf.layout.west_width);
							accordion.doLayout();
						}
					}
					else {
						westRegion.hasScrollbar = true;
					}

					// expand first panel
					accordion.getFirstPanel().expand();

					// look for url params
					var id = ns.core.web.url.getParam('id'),
						session = ns.core.web.url.getParam('s'),
						layout;

					if (id) {
						ns.core.web.pivot.loadTable(id);
					}
					else if (Ext.isString(session) && NS.isSessionStorage && Ext.isObject(JSON.parse(sessionStorage.getItem('dhis2'))) && session in JSON.parse(sessionStorage.getItem('dhis2'))) {
						layout = ns.core.api.layout.Layout(JSON.parse(sessionStorage.getItem('dhis2'))[session]);

						if (layout) {
							ns.core.web.pivot.getData(layout, true);
						}
					}

					// fade in
					Ext.defer( function() {
						Ext.getBody().fadeIn({
							duration: 500
						});
					}, 300 );
				}
			}
		});

		// add listeners
		(function() {
			ns.app.stores.indicatorAvailable.on('load', function() {
				ns.core.web.multiSelect.filterAvailable(indicatorAvailable, indicatorSelected);
			});

			ns.app.stores.dataElementAvailable.on('load', function() {
				ns.core.web.multiSelect.filterAvailable(dataElementAvailable, dataElementSelected);
			});

			ns.app.stores.dataSetAvailable.on('load', function(s) {
				ns.core.web.multiSelect.filterAvailable(dataSetAvailable, dataSetSelected);
				s.sort('name', 'ASC');
			});
		}());

		return viewport;
	};

	// initialize
	(function() {
		var requests = [],
			callbacks = 0,
			init = {},
			fn;

		fn = function() {
			if (++callbacks === requests.length) {

				NS.instances.push(ns);

				ns.core = NS.getCore(init);
				extendCore(ns.core);

				dimConf = ns.core.conf.finals.dimension;
				ns.app.viewport = createViewport();
			}
		};

		// requests
		Ext.Ajax.request({
			url: 'manifest.webapp',
			success: function(r) {
				init.contextPath = Ext.decode(r.responseText).activities.dhis.href;

				Ext.Ajax.request({
					url: 'i18n.json',
					success: function(r) {
						var i18nArray = Ext.decode(r.responseText);

						Ext.Ajax.request({
							url: init.contextPath + '/api/system/context.json',
							success: function(r) {
								init.contextPath = Ext.decode(r.responseText).contextPath || init.contextPath;

								// i18n
								requests.push({
									url: init.contextPath + '/api/i18n?package=org.hisp.dhis.pivot',
									method: 'POST',
									headers: {
										'Content-Type': 'application/json',
										'Accepts': 'application/json'
									},
									params: Ext.encode(i18nArray),
									success: function(r) {
										NS.i18n = Ext.decode(r.responseText);
										fn();
									}
								});

								// root nodes
								requests.push({
									url: init.contextPath + '/api/organisationUnits.json?level=1&paging=false&links=false&viewClass=detailed',
									success: function(r) {
										init.rootNodes = Ext.decode(r.responseText).organisationUnits || [];
										fn();
									}
								});

								// organisation unit levels
								requests.push({
									url: init.contextPath + '/api/organisationUnitLevels.json?paging=false&links=false',
									success: function(r) {
										init.organisationUnitLevels = Ext.decode(r.responseText).organisationUnitLevels || [];
										fn();
									}
								});

								// user orgunits and children
								requests.push({
									url: init.contextPath + '/api/organisationUnits.json?userOnly=true&viewClass=detailed&links=false',
									success: function(r) {
										var organisationUnits = Ext.decode(r.responseText).organisationUnits || [];

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
											alert('User is not assigned to any organisation units');
										}

										fn();
									}
								});

								// legend sets
								requests.push({
									url: init.contextPath + '/api/mapLegendSets.json?viewClass=detailed&links=false&paging=false',
									success: function(r) {
										init.legendSets = Ext.decode(r.responseText).mapLegendSets || [];
										fn();
									}
								});

								// dimensions
								requests.push({
									url: init.contextPath + '/api/dimensions.json?links=false&paging=false',
									success: function(r) {
										init.dimensions = Ext.decode(r.responseText).dimensions || [];
										fn();
									}
								});

								for (var i = 0; i < requests.length; i++) {
									Ext.Ajax.request(requests[i]);
								}
							}
						});
					}
				});
			}
		});
	}());
});
