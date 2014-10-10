Ext.onReady(function() {

	// SIMPLE REGRESSION
	function SimpleRegression()
	{
		var sumX = 0; // Sum of x values
		var sumY = 0; // Sum of y values
		var sumXX = 0; // Total variation in x
		var sumXY = 0; // Sum of products
		var n = 0; // Number of observations
		var xbar = 0; // Mean of accumulated x values, used in updating formulas
		var ybar = 0; // Mean of accumulated y values, used in updating formulas
		
		this.addData = function( x, y )
		{
			if ( n == 0 )
			{
				xbar = x;
				ybar = y;
			}
			else
			{
				var dx = x - xbar;
				var dy = y - ybar;
				sumXX += dx * dx * n / ( n + 1 );
				sumXY += dx * dy * n / ( n + 1 );
				xbar += dx / ( n + 1 );
				ybar += dy / ( n + 1 );
			}
			
			sumX += x;
			sumY += y;
			n++;
		};
		
		this.predict = function( x )
		{
			var b1 = this.getSlope();
			
			return this.getIntercept( b1 ) + b1 * x;
		};
		
		this.getSlope = function()
		{
			if ( n < 2 )
			{
				return Number.NaN;
			}
			
			return sumXY / sumXX;
		};
		
		this.getIntercept = function( slope )
		{
			return ( sumY - slope * sumX ) / n;
		};
	}

	// CORE

	// ext config
	Ext.Ajax.method = 'GET';

	// namespace
	DV = {};

	DV.instances = [];
	DV.i18n = {};
	DV.isDebug = false;
	DV.isSessionStorage = ('sessionStorage' in window && window['sessionStorage'] !== null);

	DV.getCore = function(init) {
        var conf = {},
            api = {},
            support = {},
            service = {},
            web = {},
            dimConf;

		// conf
        (function() {
            conf.finals = {
                ajax: {
					path_module: '/dhis-web-visualizer/',
					path_api: '/api/',
					path_commons: '/dhis-web-commons-ajax-json/',
                    data_get: 'chartValues.json',
                    indicator_get: 'indicatorGroups/',
                    indicator_getall: 'indicators.json?paging=false&links=false',
                    indicatorgroup_get: 'indicatorGroups.json?paging=false&links=false',
                    dataelement_get: 'dataElementGroups/',
                    dataelement_getall: 'dataElements.json?domainType=aggregate&paging=false&links=false',
                    dataelementgroup_get: 'dataElementGroups.json?paging=false&links=false',
                    dataset_get: 'dataSets.json?paging=false&links=false'
                },
                dimension: {
                    data: {
                        value: 'data',
                        name: DV.i18n.data,
                        dimensionName: 'dx',
                        objectName: 'dx'
                    },
                    indicator: {
                        value: 'indicator',
                        name: DV.i18n.indicator,
                        dimensionName: 'dx',
                        objectName: 'in'
                    },
                    dataElement: {
                        value: 'dataelement',
                        name: DV.i18n.data_element,
                        dimensionName: 'dx',
                        objectName: 'de'
                    },
                    operand: {
                        value: 'operand',
                        name: 'Operand',
                        dimensionName: 'dx',
                        objectName: 'dc'
                    },
                    dataSet: {
                        value: 'dataset',
                        name: DV.i18n.dataset,
                        dimensionName: 'dx',
                        objectName: 'ds'
                    },
                    category: {
                        name: DV.i18n.categories,
                        dimensionName: 'co',
                        objectName: 'co'
                    },
                    period: {
                        value: 'period',
                        name: DV.i18n.period,
                        dimensionName: 'pe',
                        objectName: 'pe'
                    },
                    fixedPeriod: {
                        value: 'periods'
                    },
                    relativePeriod: {
                        value: 'relativePeriods'
                    },
                    organisationUnit: {
                        value: 'organisationUnits',
                        name: DV.i18n.organisation_units,
                        dimensionName: 'ou',
                        objectName: 'ou'
                    },
                    dimension: {
                        value: 'dimension'
                        //objectName: 'di'
                    },
                    value: {
                        value: 'value'
                    }
                },
                chart: {
                    series: 'series',
                    category: 'category',
                    filter: 'filter',
                    column: 'column',
                    stackedcolumn: 'stackedcolumn',
                    bar: 'bar',
                    stackedbar: 'stackedbar',
                    line: 'line',
                    area: 'area',
                    pie: 'pie',
                    radar: 'radar'
                },
                data: {
                    domain: 'domain_',
                    targetLine: 'targetline_',
                    baseLine: 'baseline_',
                    trendLine: 'trendline_'
                },
                image: {
                    png: 'png',
                    pdf: 'pdf'
                },
                cmd: {
                    init: 'init_',
                    none: 'none_',
                    urlparam: 'id'
                },
                root: {
                    id: 'root'
                }
            };

            dimConf = conf.finals.dimension;

            dimConf.objectNameMap = {};
            dimConf.objectNameMap[dimConf.data.objectName] = dimConf.data;
            dimConf.objectNameMap[dimConf.indicator.objectName] = dimConf.indicator;
            dimConf.objectNameMap[dimConf.dataElement.objectName] = dimConf.dataElement;
            dimConf.objectNameMap[dimConf.operand.objectName] = dimConf.operand;
            dimConf.objectNameMap[dimConf.dataSet.objectName] = dimConf.dataSet;
            dimConf.objectNameMap[dimConf.category.objectName] = dimConf.category;
            dimConf.objectNameMap[dimConf.period.objectName] = dimConf.period;
            dimConf.objectNameMap[dimConf.organisationUnit.objectName] = dimConf.organisationUnit;
            dimConf.objectNameMap[dimConf.dimension.objectName] = dimConf.dimension;

			conf.period = {
				periodTypes: [
					{id: 'Monthly', name: DV.i18n.monthly},
					{id: 'Quarterly', name: DV.i18n.quarterly},
					{id: 'FinancialJuly', name: DV.i18n.financial_july},
				]
			};

            conf.layout = {
                west_width: 424,
                west_fieldset_width: 416,
                west_width_padding: 4,
                west_fill: 2,
                west_fill_accordion_indicator: 59,
                west_fill_accordion_dataelement: 59,
                west_fill_accordion_dataset: 33,
                west_fill_accordion_period: 296,
                west_fill_accordion_organisationunit: 62,
                west_maxheight_accordion_indicator: 350,
                west_maxheight_accordion_dataelement: 350,
                west_maxheight_accordion_dataset: 350,
                west_maxheight_accordion_period: 513,
                west_maxheight_accordion_organisationunit: 500,
                west_maxheight_accordion_group: 350,
                west_scrollbarheight_accordion_indicator: 300,
                west_scrollbarheight_accordion_dataelement: 300,
                west_scrollbarheight_accordion_dataset: 300,
                west_scrollbarheight_accordion_period: 450,
                west_scrollbarheight_accordion_organisationunit: 450,
                west_scrollbarheight_accordion_group: 300,
                east_tbar_height: 31,
                east_gridcolumn_height: 30,
                form_label_width: 55,
                window_favorite_ypos: 100,
                window_confirm_width: 250,
                window_share_width: 500,
                grid_favorite_width: 420,
                grid_row_height: 27,
                treepanel_minheight: 135,
                treepanel_maxheight: 400,
                treepanel_fill_default: 310,
                treepanel_toolbar_menu_width_group: 140,
                treepanel_toolbar_menu_width_level: 120,
                multiselect_minheight: 100,
                multiselect_maxheight: 250,
                multiselect_fill_default: 345,
                multiselect_fill_reportingrates: 315
            };

            conf.chart = {
                style: {
                    inset: 30,
                    fontFamily: 'Arial,Sans-serif,Lucida Grande,Ubuntu'
                },
                theme: {
                    dv1: ['#94ae0a', '#0b3b68', '#a61120', '#ff8809', '#7c7474', '#a61187', '#ffd13e', '#24ad9a', '#a66111', '#414141', '#4500c4', '#1d5700']
                }
            };

            conf.status = {
                icon: {
                    error: 'error_s.png',
                    warning: 'warning.png',
                    ok: 'ok.png'
                }
            };

        }());

        // api
        (function() {
            api.layout = {};

			api.layout.Record = function(config) {
				var config = Ext.clone(config);

				// id: string

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Record: config is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.id)) {
						alert('Record: id is not text: ' + config);
						return;
					}

					config.id = config.id.replace('.', '-');

					return config;
				}();
			};

            api.layout.Dimension = function(config) {
				var config = Ext.clone(config);

				// dimension: string

				// items: [Record]

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Dimension: config is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.dimension)) {
						console.log('Dimension: name is not a string: ' + config);
						return;
					}

					if (config.dimension !== conf.finals.dimension.category.objectName) {
						var records = [];

						if (!Ext.isArray(config.items)) {
							console.log('Dimension: items is not an array: ' + config);
							return;
						}

						for (var i = 0; i < config.items.length; i++) {
							records.push(api.layout.Record(config.items[i]));
						}

						config.items = Ext.Array.clean(records);

						if (!config.items.length) {
							console.log('Dimension: has no valid items: ' + config);
							return;
						}
					}

					return config;
				}();
			};

            api.layout.Layout = function(config) {
				var config = Ext.clone(config),
					layout = {},
					getValidatedDimensionArray,
					validateSpecialCases;

                // type: string ('column') - 'column', 'stackedcolumn', 'bar', 'stackedbar', 'line', 'area', 'pie'

                // columns: [Dimension]

                // rows: [Dimension]

                // filters: [Dimension]

                // showTrendLine: boolean (false)

                // targetLineValue: number

                // targetLineTitle: string

                // baseLineValue: number

                // baseLineTitle: string

                // showValues: boolean (true)

                // hideLegend: boolean (false)

                // hideTitle: boolean (false)

                // domainAxisTitle: string

                // rangeAxisTitle: string

                // userOrganisationUnit: boolean (false)

                // userOrganisationUnitChildren: boolean (false)

                // parentGraphMap: object

                getValidatedDimensionArray = function(dimensionArray) {
					var dimensionArray = Ext.clone(dimensionArray);

					if (!(dimensionArray && Ext.isArray(dimensionArray) && dimensionArray.length)) {
						return;
					}

					for (var i = 0; i < dimensionArray.length; i++) {
						dimensionArray[i] = api.layout.Dimension(dimensionArray[i]);
					}

					dimensionArray = Ext.Array.clean(dimensionArray);

					return dimensionArray.length ? dimensionArray : null;
				};

				analytical2layout = function(analytical) {
					var layoutConfig = Ext.clone(analytical),
						co = dimConf.category.objectName;

					analytical = Ext.clone(analytical);

					layoutConfig.columns = [];
					layoutConfig.rows = [];
					layoutConfig.filters = layoutConfig.filters || [];

					// Series
					if (Ext.isArray(analytical.columns) && analytical.columns.length) {
						analytical.columns.reverse();

						for (var i = 0, dim; i < analytical.columns.length; i++) {
							dim = analytical.columns[i];

							if (dim.dimension === co) {
								continue;
							}

							if (!layoutConfig.columns.length) {
								layoutConfig.columns.push(dim);
							}
							else {

								// indicators cannot be set as filter
								if (dim.dimension === dimConf.indicator.objectName) {
									layoutConfig.filters.push(layoutConfig.columns.pop());
									layoutConfig.columns = [dim];
								}
								else {
									layoutConfig.filters.push(dim);
								}
							}
						}
					}

					// Rows
					if (Ext.isArray(analytical.rows) && analytical.rows.length) {
						analytical.rows.reverse();

						for (var i = 0, dim; i < analytical.rows.length; i++) {
							dim = analytical.rows[i];

							if (dim.dimension === co) {
								continue;
							}

							if (!layoutConfig.rows.length) {
								layoutConfig.rows.push(dim);
							}
							else {

								// indicators cannot be set as filter
								if (dim.dimension === dimConf.indicator.objectName) {
									layoutConfig.filters.push(layoutConfig.rows.pop());
									layoutConfig.rows = [dim];
								}
								else {
									layoutConfig.filters.push(dim);
								}
							}
						}
					}

					return layoutConfig;
				};

				validateSpecialCases = function() {
					var dimConf = conf.finals.dimension,
						dimensions,
						objectNameDimensionMap = {};

					if (!layout) {
						return;
					}

					dimensions = Ext.Array.clean([].concat(layout.columns || [], layout.rows || [], layout.filters || []));

					for (var i = 0; i < dimensions.length; i++) {
						objectNameDimensionMap[dimensions[i].dimension] = dimensions[i];
					}

					if (layout.filters && layout.filters.length) {
						for (var i = 0; i < layout.filters.length; i++) {

							// Indicators as filter
							if (layout.filters[i].dimension === dimConf.indicator.objectName) {
								web.message.alert(DV.i18n.indicators_cannot_be_specified_as_filter || 'Indicators cannot be specified as filter');
								return;
							}

							// Categories as filter
							if (layout.filters[i].dimension === dimConf.category.objectName) {
								web.message.alert(DV.i18n.categories_cannot_be_specified_as_filter || 'Categories cannot be specified as filter');
								return;
							}

							// Data sets as filter
							if (layout.filters[i].dimension === dimConf.dataSet.objectName) {
								web.message.alert(DV.i18n.data_sets_cannot_be_specified_as_filter || 'Data sets cannot be specified as filter');
								return;
							}
						}
					}

					// dc and in
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.indicator.objectName]) {
						web.message.alert('Indicators and detailed data elements cannot be specified together');
						return;
					}

					// dc and de
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.dataElement.objectName]) {
						web.message.alert('Detailed data elements and totals cannot be specified together');
						return;
					}

					// dc and ds
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.dataSet.objectName]) {
						web.message.alert('Data sets and detailed data elements cannot be specified together');
						return;
					}

					// dc and co
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.category.objectName]) {
						web.message.alert('Categories and detailed data elements cannot be specified together');
						return;
					}

					return true;
				};

                return function() {
                    var objectNames = [],
						dimConf = conf.finals.dimension;

					// config must be an object
					if (!(config && Ext.isObject(config))) {
						alert('Layout: config is not an object (' + init.el + ')');
						return;
					}

                    config.columns = getValidatedDimensionArray(config.columns);
                    config.rows = getValidatedDimensionArray(config.rows);
                    config.filters = getValidatedDimensionArray(config.filters);

					// at least one dimension specified as column and row
					if (!config.columns) {
						alert('No series items selected');
						return;
					}

					if (!config.rows) {
						alert('No category items selected');
						return;
					}

					// get object names
					for (var i = 0, dims = Ext.Array.clean([].concat(config.columns || [], config.rows || [], config.filters || [])); i < dims.length; i++) {

						// Object names
						if (api.layout.Dimension(dims[i])) {
							objectNames.push(dims[i].dimension);
						}
					}

					// at least one period
					if (!Ext.Array.contains(objectNames, dimConf.period.objectName)) {
						alert('At least one period must be specified as series, category or filter');
						return;
					}

					// favorite
					if (config.id) {
						layout.id = config.id;
					}

					if (config.name) {
						layout.name = config.name;
					}

					// analytical2layout
					config = analytical2layout(config);

                    // layout
                    layout.type = Ext.isString(config.type) ? config.type.toLowerCase() : conf.finals.chart.column;

                    layout.columns = config.columns;
                    layout.rows = config.rows;
                    layout.filters = config.filters;

                    // properties
                    layout.showTrendLine = Ext.isBoolean(config.regression) ? config.regression : (Ext.isBoolean(config.showTrendLine) ? config.showTrendLine : false);
                    layout.showValues = Ext.isBoolean(config.showData) ? config.showData : (Ext.isBoolean(config.showValues) ? config.showValues : true);

                    layout.hideLegend = Ext.isBoolean(config.hideLegend) ? config.hideLegend : false;
                    layout.hideTitle = Ext.isBoolean(config.hideTitle) ? config.hideTitle : false;

                    layout.targetLineValue = Ext.isNumber(config.targetLineValue) ? config.targetLineValue : null;
                    layout.targetLineTitle = Ext.isString(config.targetLineLabel) && !Ext.isEmpty(config.targetLineLabel) ? config.targetLineLabel :
                        (Ext.isString(config.targetLineTitle) && !Ext.isEmpty(config.targetLineTitle) ? config.targetLineTitle : null);
                    layout.baseLineValue = Ext.isNumber(config.baseLineValue) ? config.baseLineValue : null;
                    layout.baseLineTitle = Ext.isString(config.baseLineLabel) && !Ext.isEmpty(config.baseLineLabel) ? config.baseLineLabel :
                        (Ext.isString(config.baseLineTitle) && !Ext.isEmpty(config.baseLineTitle) ? config.baseLineTitle : null);

                    layout.title = Ext.isString(config.title) &&  !Ext.isEmpty(config.title) ? config.title : null;
                    layout.domainAxisTitle = Ext.isString(config.domainAxisLabel) && !Ext.isEmpty(config.domainAxisLabel) ? config.domainAxisLabel :
                        (Ext.isString(config.domainAxisTitle) && !Ext.isEmpty(config.domainAxisTitle) ? config.domainAxisTitle : null);
                    layout.rangeAxisTitle = Ext.isString(config.rangeAxisLabel) && !Ext.isEmpty(config.rangeAxisLabel) ? config.rangeAxisLabel :
                        (Ext.isString(config.rangeAxisTitle) && !Ext.isEmpty(config.rangeAxisTitle) ? config.rangeAxisTitle : null);

                    layout.parentGraphMap = Ext.isObject(config.parentGraphMap) ? config.parentGraphMap : null;

					if (!validateSpecialCases()) {
						return;
					}

					return layout;
                }();
            };

            api.response = {};

            api.response.Header = function(config) {
				var config = Ext.clone(config);

				// name: string

				// meta: boolean

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Header: config is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.name)) {
						console.log('Header: name is not a string: ' + config);
						return;
					}

					if (!Ext.isBoolean(config.meta)) {
						console.log('Header: meta is not boolean: ' + config);
						return;
					}

					return config;
				}();
			};

            api.response.Response = function(config) {
				var config = Ext.clone(config);

				// headers: [Header]

				return function() {
					if (!(config && Ext.isObject(config))) {
						console.log('Response: config is not an object');
						return;
					}

					if (!(config.headers && Ext.isArray(config.headers))) {
						console.log('Response: headers is not an array');
						return;
					}

					for (var i = 0, header; i < config.headers.length; i++) {
						config.headers[i] = api.response.Header(config.headers[i]);
					}

					config.headers = Ext.Array.clean(config.headers);

					if (!config.headers.length) {
						console.log('Response: no valid headers');
						return;
					}

					if (!(Ext.isArray(config.rows) && config.rows.length > 0)) {
						alert('No values found');
						return;
					}

					if (config.headers.length !== config.rows[0].length) {
						console.log('Response: headers.length !== rows[0].length');
					}

					return config;
				}();
			};
        }());

		// support
		(function() {

			// prototype
			support.prototype = {};

				// array
			support.prototype.array = {};

			support.prototype.array.getLength = function(array, supssWarning) {
				if (!Ext.isArray(array)) {
					if (!suppressWarning) {
						console.log('support.prototype.array.getLength: not an array');
					}

					return null;
				}

				return array.length;
			};

			support.prototype.array.sort = function(array, direction, key) {
				// accepts [number], [string], [{prop: number}], [{prop: string}]

				if (!support.prototype.array.getLength(array)) {
					return;
				}

				key = key || 'name';

				array.sort( function(a, b) {

					// if object, get the property values
					if (Ext.isObject(a) && Ext.isObject(b) && key) {
						a = a[key];
						b = b[key];
					}

					// string
					if (Ext.isString(a) && Ext.isString(b)) {
						a = a.toLowerCase();
						b = b.toLowerCase();

						if (direction === 'DESC') {
							return a < b ? 1 : (a > b ? -1 : 0);
						}
						else {
							return a < b ? -1 : (a > b ? 1 : 0);
						}
					}

					// number
					else if (Ext.isNumber(a) && Ext.isNumber(b)) {
						return direction === 'DESC' ? b - a : a - b;
					}

					return 0;
				});

				return array;
			};

				// object
			support.prototype.object = {};

			support.prototype.object.getLength = function(object, suppressWarning) {
				if (!Ext.isObject(object)) {
					if (!suppressWarning) {
						console.log('support.prototype.object.getLength: not an object');
					}

					return null;
				}

				var size = 0;

				for (var key in object) {
					if (object.hasOwnProperty(key)) {
						size++;
					}
				}

				return size;
			};

			support.prototype.object.hasObject = function(object, property, value) {
				if (!support.prototype.object.getLength(object)) {
					return null;
				}

				for (var key in object) {
					var record = object[key];

					if (object.hasOwnProperty(key) && record[property] === value) {
						return true;
					}
				}

				return null;
			};

				// str
			support.prototype.str = {};

			support.prototype.str.replaceAll = function(str, find, replace) {
				return str.replace(new RegExp(find, 'g'), replace);
			};
		}());

		// service
		(function() {

			// layout
			service.layout = {};

			service.layout.cleanDimensionArray = function(dimensionArray) {
				if (!support.prototype.array.getLength(dimensionArray)) {
					return null;
				}

				var array = [];

				for (var i = 0; i < dimensionArray.length; i++) {
					array.push(api.layout.Dimension(dimensionArray[i]));
				}

				array = Ext.Array.clean(array);

				return array.length ? array : null;
			};

			service.layout.sortDimensionArray = function(dimensionArray, key) {
				if (!support.prototype.array.getLength(dimensionArray, true)) {
					return null;
				}

				// Clean dimension array
				dimensionArray = service.layout.cleanDimensionArray(dimensionArray);

				if (!dimensionArray) {
					console.log('service.layout.sortDimensionArray: no valid dimensions');
					return null;
				}

				key = key || 'dimensionName';

				// Dimension order
				Ext.Array.sort(dimensionArray, function(a,b) {
					if (a[key] < b[key]) {
						return -1;
					}
					if (a[key] > b[key]) {
						return 1;
					}
					return 0;
				});

				// Sort object items, ids
				for (var i = 0, items; i < dimensionArray.length; i++) {
					support.prototype.array.sort(dimensionArray[i].items, 'ASC', 'id');

					if (support.prototype.array.getLength(dimensionArray[i].ids)) {
						support.prototype.array.sort(dimensionArray[i].ids);
					}
				}

				return dimensionArray;
			};

			service.layout.getObjectNameDimensionMapFromDimensionArray = function(dimensionArray) {
				var map = {};

				if (!support.prototype.array.getLength(dimensionArray)) {
					return null;
				}

				for (var i = 0, dimension; i < dimensionArray.length; i++) {
					dimension = api.layout.Dimension(dimensionArray[i]);

					if (dimension) {
						map[dimension.dimension] = dimension;
					}
				}

				return support.prototype.object.getLength(map) ? map : null;
			};

			service.layout.getObjectNameDimensionItemsMapFromDimensionArray = function(dimensionArray) {
				var map = {};

				if (!support.prototype.array.getLength(dimensionArray)) {
					return null;
				}

				for (var i = 0, dimension; i < dimensionArray.length; i++) {
					dimension = api.layout.Dimension(dimensionArray[i]);

					if (dimension) {
						map[dimension.dimension] = dimension.items;
					}
				}

				return support.prototype.object.getLength(map) ? map : null;
			};

			service.layout.getExtendedLayout = function(layout) {
                var layout = Ext.clone(layout),
                    xLayout = {
                        columns: [],
                        rows: [],
                        filters: [],

                        columnObjectNames: [],
                        columnDimensionNames: [],
                        columnItems: [],
                        columnIds: [],
                        rowObjectNames: [],
                        rowDimensionNames: [],
                        rowItems: [],
                        rowIds: [],

                        // Axis
                        axisDimensions: [],
                        axisObjectNames: [],
                        axisDimensionNames: [],

                            // For param string
                        sortedAxisDimensionNames: [],

                        // Filter
                        filterDimensions: [],
                        filterObjectNames: [],
                        filterDimensionNames: [],
                        filterItems: [],
                        filterIds: [],

                            // For param string
                        sortedFilterDimensions: [],

                        // All
                        dimensions: [],
                        objectNames: [],
                        dimensionNames: [],

                        // Object name maps
                        objectNameDimensionsMap: {},
                        objectNameItemsMap: {},
                        objectNameIdsMap: {},

                        // Dimension name maps
                        dimensionNameDimensionsMap: {},
                        dimensionNameItemsMap: {},
                        dimensionNameIdsMap: {},

                            // For param string
                        dimensionNameSortedIdsMap: {}
                    };

                Ext.applyIf(xLayout, layout);

                // Columns, rows, filters
                if (layout.columns) {
                    for (var i = 0, dim, items, xDim; i < layout.columns.length; i++) {
                        dim = layout.columns[i];
                        items = dim.items;
                        xDim = {};

                        xDim.dimension = dim.dimension;
                        xDim.objectName = dim.dimension;
                        xDim.dimensionName = dimConf.objectNameMap[dim.dimension].dimensionName;

                        if (items) {
                            xDim.items = items;
                            xDim.ids = [];

                            for (var j = 0; j < items.length; j++) {
                                xDim.ids.push(items[j].id);
                            }
                        }

                        xLayout.columns.push(xDim);

                        xLayout.columnObjectNames.push(xDim.objectName);
                        xLayout.columnDimensionNames.push(xDim.dimensionName);
                        xLayout.columnItems = xLayout.columnItems.concat(xDim.items);
                        xLayout.columnIds = xLayout.columnIds.concat(xDim.ids);

                        xLayout.axisDimensions.push(xDim);
                        xLayout.axisObjectNames.push(xDim.objectName);
                        xLayout.axisDimensionNames.push(dimConf.objectNameMap[xDim.objectName].dimensionName);

                        xLayout.objectNameDimensionsMap[xDim.objectName] = xDim;
                        xLayout.objectNameItemsMap[xDim.objectName] = xDim.items;
                        xLayout.objectNameIdsMap[xDim.objectName] = xDim.ids;
                    }
                }

                if (layout.rows) {
                    for (var i = 0, dim, items, xDim; i < layout.rows.length; i++) {
                        dim = layout.rows[i];
                        items = dim.items;
                        xDim = {};

                        xDim.dimension = dim.dimension;
                        xDim.objectName = dim.dimension;
                        xDim.dimensionName = dimConf.objectNameMap[dim.dimension].dimensionName;

                        if (items) {
                            xDim.items = items;
                            xDim.ids = [];

                            for (var j = 0; j < items.length; j++) {
                                xDim.ids.push(items[j].id);
                            }
                        }

                        xLayout.rows.push(xDim);

                        xLayout.rowObjectNames.push(xDim.objectName);
                        xLayout.rowDimensionNames.push(xDim.dimensionName);
                        xLayout.rowItems = xLayout.rowItems.concat(xDim.items);
                        xLayout.rowIds = xLayout.rowIds.concat(xDim.ids);

                        xLayout.axisDimensions.push(xDim);
                        xLayout.axisObjectNames.push(xDim.objectName);
                        xLayout.axisDimensionNames.push(dimConf.objectNameMap[xDim.objectName].dimensionName);

                        xLayout.objectNameDimensionsMap[xDim.objectName] = xDim;
                        xLayout.objectNameItemsMap[xDim.objectName] = xDim.items;
                        xLayout.objectNameIdsMap[xDim.objectName] = xDim.ids;
                    }
                }

                if (layout.filters) {
                    for (var i = 0, dim, items, xDim; i < layout.filters.length; i++) {
                        dim = layout.filters[i];
                        items = dim.items;
                        xDim = {};

                        xDim.dimension = dim.dimension;
                        xDim.objectName = dim.dimension;
                        xDim.dimensionName = dimConf.objectNameMap[dim.dimension].dimensionName;

                        if (items) {
                            xDim.items = items;
                            xDim.ids = [];

                            for (var j = 0; j < items.length; j++) {
                                xDim.ids.push(items[j].id);
                            }
                        }

                        xLayout.filters.push(xDim);

                        xLayout.filterDimensions.push(xDim);
                        xLayout.filterObjectNames.push(xDim.objectName);
                        xLayout.filterDimensionNames.push(dimConf.objectNameMap[xDim.objectName].dimensionName);
                        xLayout.filterItems = xLayout.filterItems.concat(xDim.items);
                        xLayout.filterIds = xLayout.filterIds.concat(xDim.ids);

                        xLayout.objectNameDimensionsMap[xDim.objectName] = xDim;
                        xLayout.objectNameItemsMap[xDim.objectName] = xDim.items;
                        xLayout.objectNameIdsMap[xDim.objectName] = xDim.ids;
                    }
                }

                // Unique dimension names
                xLayout.axisDimensionNames = Ext.Array.unique(xLayout.axisDimensionNames);
                xLayout.filterDimensionNames = Ext.Array.unique(xLayout.filterDimensionNames);

                xLayout.columnDimensionNames = Ext.Array.unique(xLayout.columnDimensionNames);
                xLayout.rowDimensionNames = Ext.Array.unique(xLayout.rowDimensionNames);
                xLayout.filterDimensionNames = Ext.Array.unique(xLayout.filterDimensionNames);

                    // For param string
                xLayout.sortedAxisDimensionNames = Ext.clone(xLayout.axisDimensionNames).sort();
                xLayout.sortedFilterDimensions = service.layout.sortDimensionArray(Ext.clone(xLayout.filterDimensions));

                // All
                xLayout.dimensions = [].concat(xLayout.axisDimensions, xLayout.filterDimensions);
                xLayout.objectNames = [].concat(xLayout.axisObjectNames, xLayout.filterObjectNames);
                xLayout.dimensionNames = [].concat(xLayout.axisDimensionNames, xLayout.filterDimensionNames);

                // Dimension name maps
                for (var i = 0, dimName; i < xLayout.dimensionNames.length; i++) {
                    dimName = xLayout.dimensionNames[i];

                    xLayout.dimensionNameDimensionsMap[dimName] = [];
                    xLayout.dimensionNameItemsMap[dimName] = [];
                    xLayout.dimensionNameIdsMap[dimName] = [];
                }

                for (var i = 0, xDim; i < xLayout.dimensions.length; i++) {
                    xDim = xLayout.dimensions[i];

                    xLayout.dimensionNameDimensionsMap[xDim.dimensionName].push(xDim);
                    xLayout.dimensionNameItemsMap[xDim.dimensionName] = xLayout.dimensionNameItemsMap[xDim.dimensionName].concat(xDim.items);
                    xLayout.dimensionNameIdsMap[xDim.dimensionName] = xLayout.dimensionNameIdsMap[xDim.dimensionName].concat(xDim.ids);
                }

                    // For param string
                for (var key in xLayout.dimensionNameIdsMap) {
                    if (xLayout.dimensionNameIdsMap.hasOwnProperty(key)) {
                        xLayout.dimensionNameSortedIdsMap[key] = Ext.clone(xLayout.dimensionNameIdsMap[key]).sort();
                    }
                }

                return xLayout;
            };

			service.layout.getSyncronizedXLayout = function(xLayout, response) {
				var dimensions = Ext.Array.clean([].concat(xLayout.columns || [], xLayout.rows || [], xLayout.filters || [])),
					xOuDimension = xLayout.objectNameDimensionsMap[dimConf.organisationUnit.objectName],
					isUserOrgunit = xOuDimension && Ext.Array.contains(xOuDimension.ids, 'USER_ORGUNIT'),
					isUserOrgunitChildren = xOuDimension && Ext.Array.contains(xOuDimension.ids, 'USER_ORGUNIT_CHILDREN'),
					isUserOrgunitGrandChildren = xOuDimension && Ext.Array.contains(xOuDimension.ids, 'USER_ORGUNIT_GRANDCHILDREN'),
					isLevel = function() {
						if (xOuDimension && Ext.isArray(xOuDimension.ids)) {
							for (var i = 0; i < xOuDimension.ids.length; i++) {
								if (xOuDimension.ids[i].substr(0,5) === 'LEVEL') {
									return true;
								}
							}
						}

						return false;
					}(),
					isGroup = function() {
						if (xOuDimension && Ext.isArray(xOuDimension.ids)) {
							for (var i = 0; i < xOuDimension.ids.length; i++) {
								if (xOuDimension.ids[i].substr(0,8) === 'OU_GROUP') {
									return true;
								}
							}
						}

						return false;
					}(),
					ou = dimConf.organisationUnit.objectName,
					layout;

				// Set items from init/metaData/xLayout
				for (var i = 0, dim, metaDataDim, items; i < dimensions.length; i++) {
					dim = dimensions[i];
					dim.items = [];
					metaDataDim = response.metaData[dim.objectName];

					// If ou and children
					if (dim.dimensionName === ou) {
						if (isUserOrgunit || isUserOrgunitChildren || isUserOrgunitGrandChildren) {
							var userOu,
								userOuc,
								userOugc;

							if (isUserOrgunit) {
								userOu = [{
									id: init.user.ou,
									name: response.metaData.names[init.user.ou]
								}];
							}
							if (isUserOrgunitChildren) {
								userOuc = [];

								for (var j = 0; j < init.user.ouc.length; j++) {
									userOuc.push({
										id: init.user.ouc[j],
										name: response.metaData.names[init.user.ouc[j]]
									});
								}

								support.prototype.array.sort(userOuc);
							}
							if (isUserOrgunitGrandChildren) {
								var userOuOuc = [].concat(init.user.ou, init.user.ouc),
									responseOu = response.metaData[ou];

								userOugc = [];

								for (var j = 0, id; j < responseOu.length; j++) {
									id = responseOu[j];

									if (!Ext.Array.contains(userOuOuc, id)) {
										userOugc.push({
											id: id,
											name: response.metaData.names[id]
										});
									}
								}

								support.prototype.array.sort(userOugc);
							}

							dim.items = [].concat(userOu || [], userOuc || [], userOugc || []);
						}
						else if (isLevel || isGroup) {
								for (var j = 0, responseOu = response.metaData[ou], id; j < responseOu.length; j++) {
									id = responseOu[j];

									dim.items.push({
										id: id,
										name: response.metaData.names[id]
									});
								}

								support.prototype.array.sort(dim.items);
							}
							else {
								dim.items = Ext.clone(xLayout.dimensionNameItemsMap[dim.dimensionName]);
							}
					}
					else {
						// Items: get ids from metadata -> items
						if (Ext.isArray(metaDataDim) && metaDataDim.length) {
							var ids = Ext.clone(response.metaData[dim.dimensionName]);
							for (var j = 0; j < ids.length; j++) {
								dim.items.push({
									id: ids[j],
									name: response.metaData.names[ids[j]]
								});
							}
						}
						// Items: get items from xLayout
						else {
							dim.items = Ext.clone(xLayout.objectNameItemsMap[dim.objectName]);
						}
					}
				}

				// Re-layout
				layout = api.layout.Layout(xLayout);

				if (layout) {
					dimensions = Ext.Array.clean([].concat(layout.columns || [], layout.rows || [], layout.filters || []));

					for (var i = 0, idNameMap = response.metaData.names, dimItems; i < dimensions.length; i++) {
						dimItems = dimensions[i].items;

						if (Ext.isArray(dimItems) && dimItems.length) {
							for (var j = 0, item; j < dimItems.length; j++) {
								item = dimItems[j];

								if (Ext.isObject(item) && Ext.isString(idNameMap[item.id]) && !Ext.isString(item.name)) {
									item.name = idNameMap[item.id] || '';
								}
							}
						}
					}

					return service.layout.getExtendedLayout(layout);
				}

				return null;
			};

			service.layout.layout2plugin = function(layout) {
				var layout = Ext.clone(layout),
					dimensions = Ext.Array.clean([].concat(layout.columns || [], layout.rows || [], layout.filters || []));

				if (Ext.isString(layout.id)) {
					return {id: layout.id};
				}

				for (var i = 0, dimension, item; i < dimensions.length; i++) {
					dimension = dimensions[i];

					delete dimension.id;
					delete dimension.ids;
					delete dimension.type;
					delete dimension.dimensionName;
					delete dimension.objectName;

					for (var j = 0, item; j < dimension.items.length; j++) {
						item = dimension.items[j];

						delete item.name;
						delete item.code;
						delete item.created;
						delete item.lastUpdated;
					}
				}

				if (!layout.showTrendLine) {
					delete layout.showTrendLine;
				}

				if (!layout.targetLineValue) {
					delete layout.targetLineValue;
				}

				if (!layout.targetLineTitle) {
					delete layout.targetLineTitle;
				}

				if (!layout.baseLineValue) {
					delete layout.baseLineValue;
				}

				if (!layout.baseLineTitle) {
					delete layout.baseLineTitle;
				}

				if (layout.showValues) {
					delete layout.showValues;
				}

				if (!layout.hideLegend) {
					delete layout.hideLegend;
				}

				if (!layout.hideTitle) {
					delete layout.hideTitle;
				}

				if (!layout.title) {
					delete layout.title;
				}

				if (!layout.domainAxisTitle) {
					delete layout.domainAxisTitle;
				}

				if (!layout.rangeAxisTitle) {
					delete layout.rangeAxisTitle;
				}

				if (!layout.sorting) {
					delete layout.sorting;
				}

				delete layout.parentGraphMap;
				delete layout.reportingPeriod;
				delete layout.organisationUnit;
				delete layout.parentOrganisationUnit;
				delete layout.regression;
				delete layout.cumulative;
				delete layout.sortOrder;
				delete layout.topLimit;

				return layout;
			};

			// response
			service.response = {};

			service.response.getExtendedResponse = function(xLayout, response) {
				var ids = [];

				response.nameHeaderMap = {};
				response.idValueMap = {};

				// extend headers
				(function() {

					// extend headers: index, ids, size
					for (var i = 0, header; i < response.headers.length; i++) {
						header = response.headers[i];

						// index
						header.index = i;

						if (header.meta) {

							// ids
							header.ids = Ext.clone(xLayout.dimensionNameIdsMap[header.name]) || [];

							// size
							header.size = header.ids.length;

							// collect ids, used by extendMetaData
							ids = ids.concat(header.ids);
						}
					}

					// nameHeaderMap (headerName: header)
					for (var i = 0, header; i < response.headers.length; i++) {
						header = response.headers[i];

						response.nameHeaderMap[header.name] = header;
					}
				}());

				// extend metadata
				(function() {
					for (var i = 0, id, splitId ; i < ids.length; i++) {
						id = ids[i];

						if (id.indexOf('-') !== -1) {
							splitId = id.split('-');
							response.metaData.names[id] = response.metaData.names[splitId[0]] + ' ' + response.metaData.names[splitId[1]];
						}
					}
				}());

				// create value id map
				(function() {
					var valueHeaderIndex = response.nameHeaderMap[conf.finals.dimension.value.value].index,
						coHeader = response.nameHeaderMap[conf.finals.dimension.category.dimensionName],
						dx = dimConf.data.dimensionName,
						co = dimConf.category.dimensionName,
						axisDimensionNames = xLayout.axisDimensionNames,
						idIndexOrder = [];

					// idIndexOrder
					for (var i = 0; i < axisDimensionNames.length; i++) {
						idIndexOrder.push(response.nameHeaderMap[axisDimensionNames[i]].index);

						// If co exists in response and is not added in layout, add co after dx
						if (coHeader && !Ext.Array.contains(axisDimensionNames, co) && axisDimensionNames[i] === dx) {
							idIndexOrder.push(coHeader.index);
						}
					}

					// idValueMap
					for (var i = 0, row, id; i < response.rows.length; i++) {
						row = response.rows[i];
						id = '';

						for (var j = 0; j < idIndexOrder.length; j++) {
							id += row[idIndexOrder[j]];
						}

						response.idValueMap[id] = row[valueHeaderIndex];
					}
				}());

				return response;

                    //response.nameHeaderMap = {};
                    //response.idValueMap = {};
                    //ids = [];

                    //var extendHeaders = function() {
                        //// Extend headers: index, items, size
                        //for (var i = 0, header; i < response.headers.length; i++) {
                            //header = response.headers[i];

                            //// Index
                            //header.index = i;

                            //if (header.meta) {

                                //// Items
                                //header.items = Ext.clone(xLayout.dimensionNameIdsMap[header.name]) || [];

                                //// Size
                                //header.size = header.items.length;

                                //// Collect ids, used by extendMetaData
                                //ids = ids.concat(header.items);
                            //}
                        //}

                        //// nameHeaderMap (headerName: header)
                        //for (var i = 0, header; i < response.headers.length; i++) {
                            //header = response.headers[i];

                            //response.nameHeaderMap[header.name] = header;
                        //}
                    //}();

                    //var extendMetaData = function() {
                        //for (var i = 0, id, splitId ; i < ids.length; i++) {
                            //id = ids[i];

                            //if (id.indexOf('-') !== -1) {
                                //splitId = id.split('-');
                                //response.metaData.names[id] = response.metaData.names[splitId[0]] + ' ' + response.metaData.names[splitId[1]];
                            //}
                        //}
                    //}();

                    //var createValueIdMap = function() {
                        //var valueHeaderIndex = response.nameHeaderMap[conf.finals.dimension.value.value].index,
                            //coHeader = response.nameHeaderMap[conf.finals.dimension.category.dimensionName],
                            //axisDimensionNames = xLayout.axisDimensionNames,
                            //idIndexOrder = [];

                        //// idIndexOrder
                        //for (var i = 0; i < axisDimensionNames.length; i++) {
                            //idIndexOrder.push(response.nameHeaderMap[axisDimensionNames[i]].index);

                            //// If co exists in response, add co after dx
                            //if (coHeader && axisDimensionNames[i] === conf.finals.dimension.data.dimensionName) {
                                //idIndexOrder.push(coHeader.index);
                            //}
                        //}

                        //// idValueMap
                        //for (var i = 0, row, id; i < response.rows.length; i++) {
                            //row = response.rows[i];
                            //id = '';

                            //for (var j = 0; j < idIndexOrder.length; j++) {
                                //id += row[idIndexOrder[j]];
                            //}

                            //response.idValueMap[id] = parseFloat(row[valueHeaderIndex]);
                        //}
                    //}();

                    //var getMinMax = function() {
                        //var valueIndex = response.nameHeaderMap.value.index,
                            //values = [];

                        //for (var i = 0; i < response.rows.length; i++) {
                            //values.push(parseFloat(response.rows[i][valueIndex]));
                        //}

                        //response.min = Ext.Array.min(values);
                        //response.max = Ext.Array.max(values);
                    //}();

                    //return response;
			};

		}());

		// web
		(function() {

			// mask
			web.mask = {};

			web.mask.show = function(component, message) {
				if (!Ext.isObject(component)) {
					console.log('support.gui.mask.show: component not an object');
					return null;
				}

				message = message || 'Loading..';

				if (component.mask) {
					component.mask.destroy();
					component.mask = null;
				}

				component.mask = new Ext.create('Ext.LoadMask', component, {
					shadow: false,
					message: message,
					style: 'box-shadow:0',
					bodyStyle: 'box-shadow:0'
				});

				component.mask.show();
			};

			web.mask.hide = function(component) {
				if (!Ext.isObject(component)) {
					console.log('support.gui.mask.hide: component not an object');
					return null;
				}

				if (component.mask) {
					component.mask.destroy();
					component.mask = null;
				}
			};

			// message
			web.message = {};

			web.message.alert = function(message) {
				console.log(message);
			};

			// analytics
			web.analytics = {};

			web.analytics.getParamString = function(xLayout, isSorted) {
                var axisDimensionNames = isSorted ? xLayout.sortedAxisDimensionNames : xLayout.axisDimensionNames,
                    filterDimensions = isSorted ? xLayout.sortedFilterDimensions : xLayout.filterDimensions,
                    dimensionNameIdsMap = isSorted ? xLayout.dimensionNameSortedIdsMap : xLayout.dimensionNameIdsMap,
                    paramString = '?',
                    addCategoryDimension = false,
                    map = xLayout.dimensionNameItemsMap,
                    dx = dimConf.indicator.dimensionName;

                for (var i = 0, dimName, items; i < axisDimensionNames.length; i++) {
                    dimName = axisDimensionNames[i];

                    paramString += 'dimension=' + dimName;

                    items = Ext.clone(dimensionNameIdsMap[dimName]);

                    if (dimName === dx) {
                        for (var j = 0, index; j < items.length; j++) {
                            index = items[j].indexOf('-');

                            if (index > 0) {
                                addCategoryDimension = true;
                                items[j] = items[j].substr(0, index);
                            }
                        }

                        items = Ext.Array.unique(items);
                    }

                    if (dimName !== dimConf.category.dimensionName) {
                        paramString += ':' + items.join(';');
                    }

                    if (i < (axisDimensionNames.length - 1)) {
                        paramString += '&';
                    }
                }

                if (addCategoryDimension) {
                    paramString += '&dimension=' + conf.finals.dimension.category.dimensionName;
                }

                if (Ext.isArray(filterDimensions) && filterDimensions.length) {
                    for (var i = 0, dim; i < filterDimensions.length; i++) {
                        dim = filterDimensions[i];

                        paramString += '&filter=' + dim.dimensionName + ':' + dim.ids.join(';');
                    }
                }

                return paramString;
            };

			web.analytics.validateUrl = function(url) {
				var msg;

                if (Ext.isIE) {
                    msg = 'Too many items selected (url has ' + url.length + ' characters). Internet Explorer accepts maximum 2048 characters.';
                }
                else {
					var len = url.length > 8000 ? '8000' : (url.length > 4000 ? '4000' : '2000');
					msg = 'Too many items selected (url has ' + url.length + ' characters). Please reduce to less than ' + len + ' characters.';
                }

                msg += '\n\n' + 'Hint: A good way to reduce the number of items is to use relative periods and level/group organisation unit selection modes.';

                alert(msg);
			};

			// chart
			web.chart = {};

			web.chart.createChart = function(ns) {
				var xResponse = ns.app.xResponse,
					xLayout = ns.app.xLayout,

					getSyncronizedXLayout,
                    getExtendedResponse,
                    validateUrl,

                    getDefaultStore,
                    getDefaultNumericAxis,
                    getDefaultCategoryAxis,
                    getDefaultSeriesTitle,
                    getDefaultSeries,
                    getDefaultTrendLines,
                    getDefaultTargetLine,
                    getDefaultBaseLine,
                    getDefaultTips,
                    setDefaultTheme,
                    getDefaultLegend,
                    getDefaultChartTitle,
                    getDefaultChartSizeHandler,
                    getDefaultChartTitlePositionHandler,
                    getDefaultChart,

                    generator = {};

                getDefaultStore = function() {
                    var pe = conf.finals.dimension.period.dimensionName,
                        columnDimensionName = xLayout.columns[0].dimensionName,
                        rowDimensionName = xLayout.rows[0].dimensionName,

                        data = [],
                        columnIds = xLayout.columnIds,
                        rowIds = xLayout.rowIds,
                        trendLineFields = [],
                        targetLineFields = [],
                        baseLineFields = [],
                        store;

                    // Data
                    for (var i = 0, obj, category; i < rowIds.length; i++) {
                        obj = {};
                        category = rowIds[i];

                        obj[conf.finals.data.domain] = xResponse.metaData.names[category];
                        for (var j = 0, id; j < columnIds.length; j++) {
                            id = support.prototype.str.replaceAll(columnIds[j], '-', '') + support.prototype.str.replaceAll(rowIds[i], '-', '');
                            //id = columnIds[j].replace('-', '') + rowIds[i].replace('-', '');

                            obj[columnIds[j]] = parseFloat(xResponse.idValueMap[id]);
                        }

                        data.push(obj);
                    }

                    // Trend lines
                    if (xLayout.showTrendLine) {
                        for (var i = 0, regression, key; i < columnIds.length; i++) {
                            regression = new SimpleRegression();
                            key = conf.finals.data.trendLine + columnIds[i];

                            for (var j = 0; j < data.length; j++) {
                                regression.addData(j, data[j][columnIds[i]]);
                            }

                            for (var j = 0; j < data.length; j++) {
                                data[j][key] = parseFloat(regression.predict(j).toFixed(1));
                            }

                            trendLineFields.push(key);
                            xResponse.metaData.names[key] = DV.i18n.trend + ' (' + xResponse.metaData.names[columnIds[i]] + ')';
                        }
                    }

                    // Target line
                    if (Ext.isNumber(xLayout.targetLineValue) || Ext.isNumber(parseFloat(xLayout.targetLineValue))) {
                        for (var i = 0; i < data.length; i++) {
                            data[i][conf.finals.data.targetLine] = parseFloat(xLayout.targetLineValue);
                        }

                        targetLineFields.push(conf.finals.data.targetLine);
                    }

                    // Base line
                    if (Ext.isNumber(xLayout.baseLineValue) || Ext.isNumber(parseFloat(xLayout.baseLineValue))) {
                        for (var i = 0; i < data.length; i++) {
                            data[i][conf.finals.data.baseLine] = parseFloat(xLayout.baseLineValue);
                        }

                        baseLineFields.push(conf.finals.data.baseLine);
                    }

                    store = Ext.create('Ext.data.Store', {
                        fields: function() {
                            var fields = Ext.clone(columnIds);
                            fields.push(conf.finals.data.domain);
                            fields = fields.concat(trendLineFields, targetLineFields, baseLineFields);

                            return fields;
                        }(),
                        data: data
                    });

                    store.rangeFields = columnIds;
                    store.domainFields = [conf.finals.data.domain];
                    store.trendLineFields = trendLineFields;
                    store.targetLineFields = targetLineFields;
                    store.baseLineFields = baseLineFields;
                    store.numericFields = [].concat(store.rangeFields, store.trendLineFields, store.targetLineFields, store.baseLineFields);

                    store.getMaximum = function() {
                        var maximums = [];

                        for (var i = 0; i < store.numericFields.length; i++) {
                            maximums.push(store.max(store.numericFields[i]));
                        }

                        return Ext.Array.max(maximums);
                    };

                    store.getMinimum = function() {
                        var minimums = [];

                        for (var i = 0; i < store.numericFields.length; i++) {
                            minimums.push(store.max(store.numericFields[i]));
                        }

                        return Ext.Array.min(minimums);
                    };

                    store.getMaximumSum = function() {
                        var sums = [],
                            recordSum = 0;

                        store.each(function(record) {
                            recordSum = 0;

                            for (var i = 0; i < store.rangeFields.length; i++) {
                                recordSum += record.data[store.rangeFields[i]];
                            }

                            sums.push(recordSum);
                        });

                        return Ext.Array.max(sums);
                    };

                    if (DV.isDebug) {
                        console.log("data", data);
                        console.log("rangeFields", store.rangeFields);
                        console.log("domainFields", store.domainFields);
                        console.log("trendLineFields", store.trendLineFields);
                        console.log("targetLineFields", store.targetLineFields);
                        console.log("baseLineFields", store.baseLineFields);
                    }

                    return store;
                };

                getDefaultNumericAxis = function(store) {
                    var typeConf = conf.finals.chart,
                        minimum = store.getMinimum(),
                        maximum,
                        axis;

                    // Set maximum if stacked + extra line
                    if ((xLayout.type === typeConf.stackedcolumn || xLayout.type === typeConf.stackedbar) &&
                        (xLayout.showTrendLine || xLayout.targetLineValue || xLayout.baseLineValue)) {
                        var a = [store.getMaximum(), store.getMaximumSum()];
                        maximum = Math.ceil(Ext.Array.max(a) * 1.1);
                        maximum = Math.floor(maximum / 10) * 10;
                    }

                    axis = {
                        type: 'Numeric',
                        position: 'left',
                        fields: store.numericFields,
                        minimum: minimum < 0 ? minimum : 0,
                        label: {
                            renderer: Ext.util.Format.numberRenderer('0,0')
                        },
                        grid: {
                            odd: {
                                opacity: 1,
                                stroke: '#aaa',
                                'stroke-width': 0.1
                            },
                            even: {
                                opacity: 1,
                                stroke: '#aaa',
                                'stroke-width': 0.1
                            }
                        }
                    };

                    if (maximum) {
                        axis.maximum = maximum;
                    }

                    if (xLayout.rangeAxisTitle) {
                        axis.title = xLayout.rangeAxisTitle;
                    }

                    return axis;
                };

                getDefaultCategoryAxis = function(store) {
                    var axis = {
                        type: 'Category',
                        position: 'bottom',
                        fields: store.domainFields,
                        label: {
                            rotate: {
                                degrees: 330
                            }
                        }
                    };

                    if (xLayout.domainAxisTitle) {
                        axis.title = xLayout.domainAxisTitle;
                    }

                    return axis;
                };

                getDefaultSeriesTitle = function(store) {
                    var a = [];

                    for (var i = 0, id, ids; i < store.rangeFields.length; i++) {
                        id = store.rangeFields[i];
                        a.push(xResponse.metaData.names[id]);
                    }

                    return a;
                };

                getDefaultSeries = function(store) {
                    var main = {
                        type: 'column',
                        axis: 'left',
                        xField: store.domainFields,
                        yField: store.rangeFields,
                        style: {
                            opacity: 0.8,
                            lineWidth: 3
                        },
                        markerConfig: {
                            type: 'circle',
                            radius: 4
                        },
                        tips: getDefaultTips(),
                        title: getDefaultSeriesTitle(store)
                    };

                    if (xLayout.showValues) {
                        main.label = {
                            display: 'outside',
                            'text-anchor': 'middle',
                            field: store.rangeFields,
                            font: conf.chart.style.fontFamily
                        };
                    }

                    return main;
                };

                getDefaultTrendLines = function(store) {
                    var a = [];

                    for (var i = 0; i < store.trendLineFields.length; i++) {
                        a.push({
                            type: 'line',
                            axis: 'left',
                            xField: store.domainFields,
                            yField: store.trendLineFields[i],
                            style: {
                                opacity: 0.8,
                                lineWidth: 3,
                                'stroke-dasharray': 8
                            },
                            markerConfig: {
                                type: 'circle',
                                radius: 0
                            },
                            title: xResponse.metaData.names[store.trendLineFields[i]]
                        });
                    }

                    return a;
                };

                getDefaultTargetLine = function(store) {
                    return {
                        type: 'line',
                        axis: 'left',
                        xField: store.domainFields,
                        yField: store.targetLineFields,
                        style: {
                            opacity: 1,
                            lineWidth: 2,
                            'stroke-width': 1,
                            stroke: '#041423'
                        },
                        showMarkers: false,
                        title: (Ext.isString(xLayout.targetLineTitle) ? xLayout.targetLineTitle : DV.i18n.target) + ' (' + xLayout.targetLineValue + ')'
                    };
                };

                getDefaultBaseLine = function(store) {
                    return {
                        type: 'line',
                        axis: 'left',
                        xField: store.domainFields,
                        yField: store.baseLineFields,
                        style: {
                            opacity: 1,
                            lineWidth: 2,
                            'stroke-width': 1,
                            stroke: '#041423'
                        },
                        showMarkers: false,
                        title: (Ext.isString(xLayout.baseLineTitle) ? xLayout.baseLineTitle : DV.i18n.base) + ' (' + xLayout.baseLineValue + ')'
                    };
                };

                getDefaultTips = function() {
                    return {
                        trackMouse: true,
                        cls: 'dv-chart-tips',
                        renderer: function(si, item) {
                            this.update('<div style="text-align:center"><div style="font-size:17px; font-weight:bold">' + item.value[1] + '</div><div style="font-size:10px">' + si.data[conf.finals.data.domain] + '</div></div>');
                        }
                    };
                };

                setDefaultTheme = function(store) {
                    var colors = conf.chart.theme.dv1.slice(0, store.rangeFields.length);

                    if (xLayout.targetLineValue || xLayout.baseLineValue) {
                        colors.push('#051a2e');
                    }

                    if (xLayout.targetLineValue) {
                        colors.push('#051a2e');
                    }

                    if (xLayout.baseLineValue) {
                        colors.push('#051a2e');
                    }

                    Ext.chart.theme.dv1 = Ext.extend(Ext.chart.theme.Base, {
                        constructor: function(config) {
                            Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
                                seriesThemes: colors,
                                colors: colors
                            }, config));
                        }
                    });
                };

                getDefaultLegend = function(store) {
                    var itemLength = 30,
                        charLength = 7,
                        numberOfItems,
                        numberOfChars = 0,
                        str = '',
                        width,
                        isVertical = false,
                        position = 'top',
                        padding = 0;

                    if (xLayout.type === conf.finals.chart.pie) {
                        numberOfItems = store.getCount();
                        store.each(function(r) {
                            str += r.data[store.domainFields[0]];
                        });
                    }
                    else {
                        numberOfItems = store.rangeFields.length;

                        for (var i = 0, name, ids; i < store.rangeFields.length; i++) {
                            if (store.rangeFields[i].indexOf('-') !== -1) {
                                ids = store.rangeFields[i].split('-');
                                name = xResponse.metaData.names[ids[0]] + ' ' + xResponse.metaData.names[ids[1]];
                            }
                            else {
                                name = xResponse.metaData.names[store.rangeFields[i]];
                            }

                            str += name;
                        }
                    }

                    numberOfChars = str.length;

                    width = (numberOfItems * itemLength) + (numberOfChars * charLength);

                    if (width > ns.app.centerRegion.getWidth() - 50) {
                        isVertical = true;
                        position = 'right';
                    }

                    if (position === 'right') {
                        padding = 5;
                    }

                    return Ext.create('Ext.chart.Legend', {
                        position: position,
                        isVertical: isVertical,
                        labelFont: '13px ' + conf.chart.style.fontFamily,
                        boxStroke: '#ffffff',
                        boxStrokeWidth: 0,
                        padding: padding
                    });
                };

                getDefaultChartTitle = function(store) {
                    var ids = xLayout.filterIds,
                        a = [],
                        text = '',
                        fontSize;

                    if (xLayout.type === conf.finals.chart.pie) {
                        ids = ids.concat(xLayout.columnIds);
                    }

                    if (Ext.isArray(ids) && ids.length) {
                        for (var i = 0; i < ids.length; i++) {
                            text += xResponse.metaData.names[ids[i]];
                            text += i < ids.length - 1 ? ', ' : '';
                        }
                    }

                    if (xLayout.title) {
                        text = xLayout.title;
                    }

                    fontSize = (ns.app.centerRegion.getWidth() / text.length) < 11.6 ? 13 : 18;

                    return Ext.create('Ext.draw.Sprite', {
                        type: 'text',
                        text: text,
                        font: 'bold ' + fontSize + 'px ' + conf.chart.style.fontFamily,
                        fill: '#111',
                        height: 20,
                        y: 	20
                    });
                };

                getDefaultChartSizeHandler = function() {
                    return function() {
						this.animate = false;
                        this.setWidth(ns.app.centerRegion.getWidth() - 15);
                        this.setHeight(ns.app.centerRegion.getHeight() - 40);
                        this.animate = true;
                    };
                };

                getDefaultChartTitlePositionHandler = function() {
                    return function() {
                        if (this.items) {
                            var title = this.items[0],
                                legend = this.legend,
                                legendCenterX,
                                titleX;

                            if (this.legend.position === 'top') {
                                legendCenterX = legend.x + (legend.width / 2);
                                titleX = legendCenterX - (title.el.getWidth() / 2);
                            }
                            else {
                                var legendWidth = legend ? legend.width : 0;
                                titleX = (this.width / 2) - (title.el.getWidth() / 2);
                            }

                            title.setAttributes({
                                x: titleX
                            }, true);
                        }
                    };
                };

                getDefaultChart = function(store, axes, series, theme) {
                    var chart,
                        config = {
							//renderTo: init.el,
                            store: store,
                            axes: axes,
                            series: series,
                            animate: true,
                            shadow: false,
                            insetPadding: 35,
                            width: ns.app.centerRegion.getWidth() - 15,
                            height: ns.app.centerRegion.getHeight() - 40,
                            theme: theme || 'dv1'
                        };

                    // Legend
                    if (!xLayout.hideLegend) {
                        config.legend = getDefaultLegend(store);

                        if (config.legend.position === 'right') {
                            config.insetPadding = 40;
                        }
                    }

                    // Title
                    if (!xLayout.hideTitle) {
                        config.items = [getDefaultChartTitle(store)];
                    }
                    else {
                        config.insetPadding = 10;
                    }

                    chart = Ext.create('Ext.chart.Chart', config);

                    chart.setChartSize = getDefaultChartSizeHandler();
                    chart.setTitlePosition = getDefaultChartTitlePositionHandler();

                    chart.onViewportResize = function() {
                        chart.setChartSize();
                        chart.redraw();
                        chart.setTitlePosition();
                    };

                    chart.on('afterrender', function() {
                        chart.setTitlePosition();
                    });

                    return chart;
                };

                generator.column = function() {
                    var store = getDefaultStore(),
                        numericAxis = getDefaultNumericAxis(store),
                        categoryAxis = getDefaultCategoryAxis(store),
                        axes = [numericAxis, categoryAxis],
                        series = [getDefaultSeries(store)];

                    // Options
                    if (xLayout.showTrendLine) {
                        series = getDefaultTrendLines(store).concat(series);
                    }

                    if (xLayout.targetLineValue) {
                        series.push(getDefaultTargetLine(store));
                    }

                    if (xLayout.baseLineValue) {
                        series.push(getDefaultBaseLine(store));
                    }

                    // Theme
                    setDefaultTheme(store);

                    return getDefaultChart(store, axes, series);
                };

                generator.stackedcolumn = function() {
                    var chart = this.column();

                    for (var i = 0, item; i < chart.series.items.length; i++) {
                        item = chart.series.items[i];

                        if (item.type === conf.finals.chart.column) {
                            item.stacked = true;
                        }
                    }

                    return chart;
                };

                generator.bar = function() {
                    var store = getDefaultStore(),
                        numericAxis = getDefaultNumericAxis(store),
                        categoryAxis = getDefaultCategoryAxis(store),
                        axes,
                        series = getDefaultSeries(store),
                        trendLines,
                        targetLine,
                        baseLine,
                        chart;

                    // Axes
                    numericAxis.position = 'bottom';
                    categoryAxis.position = 'left';
                    axes = [numericAxis, categoryAxis];

                    // Series
                    series.type = 'bar';
                    series.axis = 'bottom';

                    // Options
                    if (xLayout.showValues) {
                        series.label = {
                            display: 'outside',
                            'text-anchor': 'middle',
                            field: store.rangeFields
                        };
                    }

                    series = [series];

                    if (xLayout.showTrendLine) {
                        trendLines = getDefaultTrendLines(store);

                        for (var i = 0; i < trendLines.length; i++) {
                            trendLines[i].axis = 'bottom';
                            trendLines[i].xField = store.trendLineFields[i];
                            trendLines[i].yField = store.domainFields;
                        }

                        series = trendLines.concat(series);
                    }

                    if (xLayout.targetLineValue) {
                        targetLine = getDefaultTargetLine(store);
                        targetLine.axis = 'bottom';
                        targetLine.xField = store.targetLineFields;
                        targetLine.yField = store.domainFields;

                        series.push(targetLine);
                    }

                    if (xLayout.baseLineValue) {
                        baseLine = getDefaultBaseLine(store);
                        baseLine.axis = 'bottom';
                        baseLine.xField = store.baseLineFields;
                        baseLine.yField = store.domainFields;

                        series.push(baseLine);
                    }

                    // Theme
                    setDefaultTheme(store);

                    return getDefaultChart(store, axes, series);
                };

                generator.stackedbar = function() {
                    var chart = this.bar();

                    for (var i = 0, item; i < chart.series.items.length; i++) {
                        item = chart.series.items[i];

                        if (item.type === conf.finals.chart.bar) {
                            item.stacked = true;
                        }
                    }

                    return chart;
                };

                generator.line = function() {
                    var store = getDefaultStore(),
                        numericAxis = getDefaultNumericAxis(store),
                        categoryAxis = getDefaultCategoryAxis(store),
                        axes = [numericAxis, categoryAxis],
                        series = [],
                        colors = conf.chart.theme.dv1.slice(0, store.rangeFields.length),
                        seriesTitles = getDefaultSeriesTitle(store);

                    // Series
                    for (var i = 0, line; i < store.rangeFields.length; i++) {
                        line = {
                            type: 'line',
                            axis: 'left',
                            xField: store.domainFields,
                            yField: store.rangeFields[i],
                            style: {
                                opacity: 0.8,
                                lineWidth: 3
                            },
                            markerConfig: {
                                type: 'circle',
                                radius: 4
                            },
                            tips: getDefaultTips(),
                            title: seriesTitles[i]
                        };

                        //if (xLayout.showValues) {
                            //line.label = {
                                //display: 'over',
                                //field: store.rangeFields[i]
                            //};
                        //}

                        series.push(line);
                    }

                    // Options, theme colors
                    if (xLayout.showTrendLine) {
                        series = getDefaultTrendLines(store).concat(series);

                        colors = colors.concat(colors);
                    }

                    if (xLayout.targetLineValue) {
                        series.push(getDefaultTargetLine(store));

                        colors.push('#051a2e');
                    }

                    if (xLayout.baseLineValue) {
                        series.push(getDefaultBaseLine(store));

                        colors.push('#051a2e');
                    }

                    // Theme
                    Ext.chart.theme.dv1 = Ext.extend(Ext.chart.theme.Base, {
                        constructor: function(config) {
                            Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
                                seriesThemes: colors,
                                colors: colors
                            }, config));
                        }
                    });

                    return getDefaultChart(store, axes, series);
                };

                generator.area = function() {
                    var store = getDefaultStore(),
                        numericAxis = getDefaultNumericAxis(store),
                        categoryAxis = getDefaultCategoryAxis(store),
                        axes = [numericAxis, categoryAxis],
                        series = getDefaultSeries(store);

                    series.type = 'area';
                    series.style.opacity = 0.7;
                    series.style.lineWidth = 0;
                    delete series.label;
                    delete series.tips;
                    series = [series];

                    // Options
                    if (xLayout.showTrendLine) {
                        series = getDefaultTrendLines(store).concat(series);
                    }

                    if (xLayout.targetLineValue) {
                        series.push(getDefaultTargetLine(store));
                    }

                    if (xLayout.baseLineValue) {
                        series.push(getDefaultBaseLine(store));
                    }

                    // Theme
                    setDefaultTheme(store);

                    return getDefaultChart(store, axes, series);
                };

                generator.pie = function() {
                    var store = getDefaultStore(),
                        series,
                        colors,
                        chart,
                        label = {
                            field: conf.finals.data.domain
                        };

                    // Label
                    if (xLayout.showValues) {
                        label.display = 'middle';
                        label.contrast = true;
                        label.font = '14px ' + conf.chart.style.fontFamily;
                        label.renderer = function(value) {
                            var record = store.getAt(store.findExact(conf.finals.data.domain, value));
                            return record.data[store.rangeFields[0]];
                        };
                    }

                    // Series
                    series = [{
                        type: 'pie',
                        field: store.rangeFields[0],
                        donut: 7,
                        showInLegend: true,
                        highlight: {
                            segment: {
                                margin: 5
                            }
                        },
                        label: label,
                        style: {
                            opacity: 0.8,
                            stroke: '#555'
                        },
                        tips: {
                            trackMouse: true,
                            cls: 'dv-chart-tips',
                            renderer: function(item) {
                                this.update('<div style="text-align:center"><div style="font-size:17px; font-weight:bold">' + item.data[store.rangeFields[0]] + '</div><div style="font-size:10px">' + item.data[conf.finals.data.domain] + '</div></div>');
                            }
                        }
                    }];

                    // Theme
                    colors = conf.chart.theme.dv1.slice(0, xResponse.nameHeaderMap[xLayout.rowDimensionNames[0]].ids.length);

                    Ext.chart.theme.dv1 = Ext.extend(Ext.chart.theme.Base, {
                        constructor: function(config) {
                            Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
                                seriesThemes: colors,
                                colors: colors
                            }, config));
                        }
                    });

                    // Chart
                    chart = getDefaultChart(store, null, series);
                    //chart.legend.position = 'right';
                    //chart.legend.isVertical = true;
                    chart.insetPadding = 40;
                    chart.shadow = true;

                    return chart;
                };

                generator.radar = function() {
                    var store = getDefaultStore(),
                        axes = [],
                        series = [],
                        seriesTitles = getDefaultSeriesTitle(store),
                        chart;

                    // Axes
                    axes.push({
                        type: 'Radial',
                        position: 'radial',
                        label: {
                            display: true
                        }
                    });

                    // Series
                    for (var i = 0, obj; i < store.rangeFields.length; i++) {
                        obj = {
                            showInLegend: true,
                            type: 'radar',
                            xField: store.domainFields,
                            yField: store.rangeFields[i],
                            style: {
                                opacity: 0.5
                            },
                            tips: getDefaultTips(),
                            title: seriesTitles[i]
                        };

                        if (xLayout.showValues) {
                            obj.label = {
                                display: 'over',
                                field: store.rangeFields[i]
                            };
                        }

                        series.push(obj);
                    }

                    chart = getDefaultChart(store, axes, series, 'Category2');

                    chart.insetPadding = 40;
                    chart.height = ns.app.centerRegion.getHeight() - 80;

                    chart.setChartSize = function() {
                        this.animate = false;
                        this.setWidth(ns.app.centerRegion.getWidth());
                        this.setHeight(ns.app.centerRegion.getHeight() - 80);
                        this.animate = true;
                    };

                    return chart;
                };

                // initialize
                return generator[xLayout.type]();
            };

        }());

		// extend init
		(function() {

			// sort and extend dynamic dimensions
			if (Ext.isArray(init.dimensions)) {
				support.prototype.array.sort(init.dimensions);

				for (var i = 0, dim; i < init.dimensions.length; i++) {
					dim = init.dimensions[i];
					dim.dimensionName = dim.id;
					dim.objectName = conf.finals.dimension.dimension.objectName;
					conf.finals.dimension.objectNameMap[dim.id] = dim;
				}
			}

			// sort ouc
			support.prototype.array.sort(init.user.ouc);
		}());

		// instance
		return {
			conf: conf,
			api: api,
			support: support,
			service: service,
			web: web,
			init: init
		};
    };

	// chart tips css
	var css = '.dv-chart-tips { border-radius: 2px; padding: 0px 3px 1px; border: 2px solid #000; background-color: #000; } \n';
	css += '.dv-chart-tips .x-tip-body { background-color: #000; font-size: 13px; font-weight: normal; color: #fff; -webkit-text-stroke: 0; } \n';
	css += '.dv-chart-tips .x-tip-body div { font-family: arial,sans-serif,ubuntu,consolas !important; } \n';

	// load mask css
	css += '.x-mask-msg { padding: 0; \n border: 0 none; background-image: none; background-color: transparent; } \n';
	css += '.x-mask-msg div { background-position: 11px center; } \n';
	css += '.x-mask-msg .x-mask-loading { border: 0 none; \n background-color: #000; color: #fff; border-radius: 2px; padding: 12px 14px 12px 30px; opacity: 0.65; } \n';

	Ext.util.CSS.createStyleSheet(css);

	// i18n
	DV.i18n = {
		target: 'Target',
		base: 'Base',
		trend: 'Trend'
	};

    DV.plugin = {};

	var init = {
			user: {}
		},
		configs = [],
		isInitStarted = false,
		isInitComplete = false,
		getInit,
		execute;

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

	execute = function(config) {
		var validateConfig,
            extendInstance,
			createViewport,
			initialize,
			ns = {
				core: {},
				app: {}
			};

		validateConfig = function(config) {
			if (!Ext.isObject(config)) {
				console.log('Chart configuration is not an object');
				return;
			}

			if (!Ext.isString(config.el)) {
				console.log('No element id provided');
				return;
			}

			config.id = config.id || config.uid;

			return true;
		};

        extendInstance = function(ns) {
            var init = ns.core.init,
				api = ns.core.api,
				support = ns.core.support,
				service = ns.core.service,
				web = ns.core.web;

			init.el = config.el;

			web.chart = web.chart || {};

            web.chart.loadChart = function(id) {
				if (!Ext.isString(id)) {
					alert('Invalid chart id');
					return;
				}

				Ext.data.JsonP.request({
					url: init.contextPath + '/api/charts/' + id + '.jsonp?viewClass=dimensional&links=false',
					failure: function(r) {
						window.open(init.contextPath + '/api/charts/' + id + '.json?viewClass=dimensional&links=false', '_blank');
					},
					success: function(r) {
						var layout = api.layout.Layout(r);

						if (layout) {
							web.chart.getData(layout, true);
						}
					}
				});
			};

			web.chart.getData = function(layout, isUpdateGui) {
				var xLayout,
					paramString;

				if (!layout) {
					return;
				}

				xLayout = service.layout.getExtendedLayout(layout);
				paramString = web.analytics.getParamString(xLayout, true);

				// show mask
				web.mask.show(ns.app.centerRegion);

				Ext.data.JsonP.request({
					url: init.contextPath + '/api/analytics.jsonp' + paramString,
					timeout: 60000,
					headers: {
						'Content-Type': 'application/json',
						'Accepts': 'application/json'
					},
					disableCaching: false,
					failure: function(r) {
						web.mask.hide(ns.app.centerRegion);

						window.open(init.contextPath + '/api/analytics.json' + paramString, '_blank');
					},
					success: function(r) {
						var response = api.response.Response(r);

						if (!response) {
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

						web.chart.getChart(layout, xLayout, response, isUpdateGui);
					}
				});
			};

			web.chart.getChart = function(layout, xLayout, response, isUpdateGui) {
				var xResponse,
					xColAxis,
					xRowAxis,
					config;

				if (!xLayout) {
					xLayout = service.layout.getExtendedLayout(layout);
				}

				// extend response
				xResponse = service.response.getExtendedResponse(xLayout, response);

				// references
				ns.app.layout = layout;
				ns.app.xLayout = xLayout;
				ns.app.response = response;
				ns.app.xResponse = xResponse;

				// create chart
				ns.app.chart = ns.core.web.chart.createChart(ns);

				// update viewport
				ns.app.centerRegion.removeAll();
				ns.app.centerRegion.add(ns.app.chart);

				web.mask.hide(ns.app.centerRegion);
			};
		};

		createViewport = function() {			
			var el = Ext.get(ns.core.init.el),
				setFavorite,
				centerRegion,
				elBorderW = parseInt(el.getStyle('border-left-width')) + parseInt(el.getStyle('border-right-width')),
				elBorderH = parseInt(el.getStyle('border-top-width')) + parseInt(el.getStyle('border-bottom-width')),
				elPaddingW = parseInt(el.getStyle('padding-left')) + parseInt(el.getStyle('padding-right')),
				elPaddingH = parseInt(el.getStyle('padding-top')) + parseInt(el.getStyle('padding-bottom')),
				width = el.getWidth() - elBorderW - elPaddingW,
				height = el.getHeight() - elBorderH - elPaddingH;

			centerRegion = Ext.create('Ext.panel.Panel', {
				renderTo: el,
				bodyStyle: 'border: 0 none',
				width: config.width || width,
				height: config.height || height,
				layout: 'fit'
			});

			return {
				centerRegion: centerRegion
			};
		};

		initialize = function() {
			if (!validateConfig(config)) {
				return;
			}

			ns.core = DV.getCore(Ext.clone(init));
			extendInstance(ns);

			ns.app.viewport = createViewport();
			ns.app.centerRegion = ns.app.viewport.centerRegion;

			if (config.id) {
				ns.core.web.chart.loadChart(config.id);
			}
			else {
				layout = ns.core.api.layout.Layout(config);

				if (!layout) {
					return;
				}

				ns.core.web.chart.getData(layout);
			}
		}();
	};

	DV.plugin.getChart = function(config) {
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
	DHIS.getChart = DV.plugin.getChart;
});
