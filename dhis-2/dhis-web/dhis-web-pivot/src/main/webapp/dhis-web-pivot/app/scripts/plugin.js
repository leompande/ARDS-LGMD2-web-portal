Ext.onReady( function() {

	// CORE

	// ext config
	Ext.Ajax.method = 'GET';

	// namespace
	PT = {};

	PT.instances = [];
	PT.i18n = {};
	PT.isDebug = false;
	PT.isSessionStorage = ('sessionStorage' in window && window['sessionStorage'] !== null);

	PT.getCore = function(init) {
        var conf = {},
            api = {},
            support = {},
            service = {},
            web = {},
            dimConf;

		// conf
		(function() {
			conf.finals = {
				url: {
					path_module: '/dhis-web-pivot/',
					organisationunitchildren_get: 'getOrganisationUnitChildren.action'
				},
				dimension: {
					data: {
						value: 'data',
						name: PT.i18n.data,
						dimensionName: 'dx',
						objectName: 'dx',
						warning: {
							filter: '...'//PT.i18n.wm_multiple_filter_ind_de
						}
					},
					category: {
						name: PT.i18n.categories,
						dimensionName: 'co',
						objectName: 'co',
					},
					indicator: {
						value: 'indicators',
						name: PT.i18n.indicators,
						dimensionName: 'dx',
						objectName: 'in'
					},
					dataElement: {
						value: 'dataElements',
						name: PT.i18n.data_elements,
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
						value: 'dataSets',
						name: PT.i18n.data_sets,
						dimensionName: 'dx',
						objectName: 'ds'
					},
					period: {
						value: 'period',
						name: PT.i18n.periods,
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
						name: PT.i18n.organisation_units,
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
					{id: 'Monthly', name: PT.i18n.monthly},
					{id: 'Quarterly', name: PT.i18n.quarterly},
					{id: 'FinancialJuly', name: PT.i18n.financial_july}
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
				west_maxheight_accordion_indicator: 400,
				west_maxheight_accordion_dataelement: 400,
				west_maxheight_accordion_dataset: 400,
				west_maxheight_accordion_period: 513,
				west_maxheight_accordion_organisationunit: 900,
				west_maxheight_accordion_group: 340,
				west_maxheight_accordion_options: 449,
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

			conf.pivot = {
				digitGroupSeparator: {
					'comma': ',',
					'space': ' '
				},
				displayDensity: {
					'compact': '3px',
					'normal': '5px',
					'comfortable': '10px',
				},
				fontSize: {
					'small': '10px',
					'normal': '11px',
					'large': '13px'
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

				// columns: [Dimension]

				// rows: [Dimension]

				// filters: [Dimension]

				// showTotals: boolean (true)

				// showSubTotals: boolean (true)

				// hideEmptyRows: boolean (false)

				// showHierarchy: boolean (false)

				// displayDensity: string ('normal') - 'compact', 'normal', 'comfortable'

				// fontSize: string ('normal') - 'small', 'normal', 'large'

				// digitGroupSeparator: string ('space') - 'none', 'comma', 'space'

				// legendSet: object

				// parentGraphMap: object

				// sorting: transient object

				// reportingPeriod: boolean (false) //report tables only

				// organisationUnit: boolean (false) //report tables only

				// parentOrganisationUnit: boolean (false) //report tables only

				// regression: boolean (false)

				// cumulative: boolean (false)

				// sortOrder: integer (0) //-1, 0, 1

				// topLimit: integer (100) //5, 10, 20, 50, 100

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
								web.message.alert(PT.i18n.indicators_cannot_be_specified_as_filter || 'Indicators cannot be specified as filter');
								return;
							}

							// Categories as filter
							if (layout.filters[i].dimension === dimConf.category.objectName) {
								web.message.alert(PT.i18n.categories_cannot_be_specified_as_filter || 'Categories cannot be specified as filter');
								return;
							}

							// Data sets as filter
							if (layout.filters[i].dimension === dimConf.dataSet.objectName) {
								web.message.alert(PT.i18n.data_sets_cannot_be_specified_as_filter || 'Data sets cannot be specified as filter');
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

					// at least one dimension specified as column or row
					if (!(config.columns || config.rows)) {
						alert(PT.i18n.at_least_one_dimension_must_be_specified_as_row_or_column);
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
						alert(PT.i18n.at_least_one_period_must_be_specified_as_column_row_or_filter);
						return;
					}

					// favorite
					if (config.id) {
						layout.id = config.id;
					}

					if (config.name) {
						layout.name = config.name;
					}

					// layout
					layout.columns = config.columns;
					layout.rows = config.rows;
					layout.filters = config.filters;

					// properties
					layout.showTotals = Ext.isBoolean(config.totals) ? config.totals : (Ext.isBoolean(config.showTotals) ? config.showTotals : true);
					layout.showSubTotals = Ext.isBoolean(config.subtotals) ? config.subtotals : (Ext.isBoolean(config.showSubTotals) ? config.showSubTotals : true);
					layout.hideEmptyRows = Ext.isBoolean(config.hideEmptyRows) ? config.hideEmptyRows : false;

					layout.showHierarchy = Ext.isBoolean(config.showHierarchy) ? config.showHierarchy : false;

					layout.displayDensity = Ext.isString(config.displayDensity) && !Ext.isEmpty(config.displayDensity) ? config.displayDensity : 'normal';
					layout.fontSize = Ext.isString(config.fontSize) && !Ext.isEmpty(config.fontSize) ? config.fontSize : 'normal';
					layout.digitGroupSeparator = Ext.isString(config.digitGroupSeparator) && !Ext.isEmpty(config.digitGroupSeparator) ? config.digitGroupSeparator : 'space';
					layout.legendSet = Ext.isObject(config.legendSet) && Ext.isString(config.legendSet.id) ? config.legendSet : null;

					layout.parentGraphMap = Ext.isObject(config.parentGraphMap) ? config.parentGraphMap : null;

					layout.sorting = Ext.isObject(config.sorting) && Ext.isString(config.sorting.id) && Ext.isString(config.sorting.direction) ? config.sorting : null;

					layout.reportingPeriod = Ext.isObject(config.reportParams) && Ext.isBoolean(config.reportParams.paramReportingPeriod) ? config.reportParams.paramReportingPeriod : (Ext.isBoolean(config.reportingPeriod) ? config.reportingPeriod : false);
					layout.organisationUnit =  Ext.isObject(config.reportParams) && Ext.isBoolean(config.reportParams.paramOrganisationUnit) ? config.reportParams.paramOrganisationUnit : (Ext.isBoolean(config.organisationUnit) ? config.organisationUnit : false);
					layout.parentOrganisationUnit =  Ext.isObject(config.reportParams) && Ext.isBoolean(config.reportParams.paramParentOrganisationUnit) ? config.reportParams.paramParentOrganisationUnit : (Ext.isBoolean(config.parentOrganisationUnit) ? config.parentOrganisationUnit : false);

					layout.regression = Ext.isBoolean(config.regression) ? config.regression : false;
					layout.cumulative = Ext.isBoolean(config.cumulative) ? config.cumulative : false;
					layout.sortOrder = Ext.isNumber(config.sortOrder) ? config.sortOrder : 0;
					layout.topLimit = Ext.isNumber(config.topLimit) ? config.topLimit : 0;

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

			support.prototype.array.getLength = function(array, suppressWarning) {
				if (!Ext.isArray(array)) {
					if (!suppressWarning) {
						console.log('support.prototype.array.getLength: not an array');
					}

					return null;
				}

				return array.length;
			};

			support.prototype.array.sort = function(array, direction, key) {
				// accepts [number], [string], [{key: number}], [{key: string}]

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

					return -1;
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

			support.prototype.str.toggleDirection = function(direction) {
				return direction === 'DESC' ? 'ASC' : 'DESC';
			};

				// number
			support.prototype.number = {};

			support.prototype.number.getNumberOfDecimals = function(number) {
				var str = new String(number);
				return (str.indexOf('.') > -1) ? (str.length - str.indexOf('.') - 1) : 0;
			};

			support.prototype.number.roundIf = function(number, precision) {
				number = parseFloat(number);
				precision = parseFloat(precision);

				if (Ext.isNumber(number) && Ext.isNumber(precision)) {
					var numberOfDecimals = support.prototype.number.getNumberOfDecimals(number);
					return numberOfDecimals > precision ? Ext.Number.toFixed(number, precision) : number;
				}

				return number;
			};

			support.prototype.number.prettyPrint = function(number, separator) {
				separator = separator || 'space';

				if (separator === 'none') {
					return number;
				}

				return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, conf.pivot.digitGroupSeparator[separator]);
			};

			// color
			support.color = {};

			support.color.hexToRgb = function(hex) {
				var shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i,
					result;

				hex = hex.replace(shorthandRegex, function(m, r, g, b) {
					return r + r + g + g + b + b;
				});

				result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);

				return result ? {
					r: parseInt(result[1], 16),
					g: parseInt(result[2], 16),
					b: parseInt(result[3], 16)
				} : null;
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

			service.layout.getItemName = function(layout, response, id, isHtml) {
				var metaData = response.metaData,
					name = '';

				if (service.layout.isHierarchy(layout, response, id)) {
					var a = Ext.Array.clean(metaData.ouHierarchy[id].split('/'));
					a.shift();

					for (var i = 0; i < a.length; i++) {
						name += (isHtml ? '<span class="text-weak">' : '') + metaData.names[a[i]] + (isHtml ? '</span>' : '') + ' / ';
					}
				}

				name += metaData.names[id];

				return name;
			};

			service.layout.getExtendedLayout = function(layout) {
				var layout = Ext.clone(layout),
					xLayout;

				xLayout = {
					columns: [],
					rows: [],
					filters: [],

					columnObjectNames: [],
					columnDimensionNames: [],
					rowObjectNames: [],
					rowDimensionNames: [],

					// axis
					axisDimensions: [],
					axisObjectNames: [],
					axisDimensionNames: [],

						// for param string
					sortedAxisDimensionNames: [],

					// Filter
					filterDimensions: [],
					filterObjectNames: [],
					filterDimensionNames: [],

						// for param string
					sortedFilterDimensions: [],

					// all
					dimensions: [],
					objectNames: [],
					dimensionNames: [],

					// oject name maps
					objectNameDimensionsMap: {},
					objectNameItemsMap: {},
					objectNameIdsMap: {},

					// dimension name maps
					dimensionNameDimensionsMap: {},
					dimensionNameItemsMap: {},
					dimensionNameIdsMap: {},

						// for param string
					dimensionNameSortedIdsMap: {},

					// sort table by column
					sortableIdObjects: []
				};

				Ext.applyIf(xLayout, layout);

				// columns, rows, filters
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
						dim = Ext.clone(layout.rows[i]);
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

						xLayout.objectNameDimensionsMap[xDim.objectName] = xDim;
						xLayout.objectNameItemsMap[xDim.objectName] = xDim.items;
						xLayout.objectNameIdsMap[xDim.objectName] = xDim.ids;
					}
				}

				// legend set
				xLayout.legendSet = layout.legendSet ? init.idLegendSetMap[layout.legendSet.id] : null;

				if (layout.legendSet) {
					xLayout.legendSet = init.idLegendSetMap[layout.legendSet.id];
					support.prototype.array.sort(xLayout.legendSet.mapLegends, 'ASC', 'startValue');
				}

				// unique dimension names
				xLayout.axisDimensionNames = Ext.Array.unique(xLayout.axisDimensionNames);
				xLayout.filterDimensionNames = Ext.Array.unique(xLayout.filterDimensionNames);

				xLayout.columnDimensionNames = Ext.Array.unique(xLayout.columnDimensionNames);
				xLayout.rowDimensionNames = Ext.Array.unique(xLayout.rowDimensionNames);
				xLayout.filterDimensionNames = Ext.Array.unique(xLayout.filterDimensionNames);

					// for param string
				xLayout.sortedAxisDimensionNames = Ext.clone(xLayout.axisDimensionNames).sort();
				xLayout.sortedFilterDimensions = service.layout.sortDimensionArray(Ext.clone(xLayout.filterDimensions));

				// all
				xLayout.dimensions = [].concat(xLayout.axisDimensions, xLayout.filterDimensions);
				xLayout.objectNames = [].concat(xLayout.axisObjectNames, xLayout.filterObjectNames);
				xLayout.dimensionNames = [].concat(xLayout.axisDimensionNames, xLayout.filterDimensionNames);

				// dimension name maps
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

					// for param string
				for (var key in xLayout.dimensionNameIdsMap) {
					if (xLayout.dimensionNameIdsMap.hasOwnProperty(key)) {
						xLayout.dimensionNameSortedIdsMap[key] = Ext.clone(xLayout.dimensionNameIdsMap[key]).sort();
					}
				}

				// Uuid
				xLayout.tableUuid = init.el + '_' + Ext.data.IdGenerator.get('uuid').generate();

				return xLayout;
			};

			service.layout.getSyncronizedXLayout = function(xLayout, response) {
				var removeDimensionFromXLayout,
					getHeaderNames,
					dimensions = Ext.Array.clean([].concat(xLayout.columns || [], xLayout.rows || [], xLayout.filters || []));

				removeDimensionFromXLayout = function(objectName) {
					var getUpdatedAxis;

					getUpdatedAxis = function(axis) {
						var dimension;
						axis = Ext.clone(axis);

						for (var i = 0; i < axis.length; i++) {
							if (axis[i].dimension === objectName) {
								dimension = axis[i];
							}
						}

						if (dimension) {
							Ext.Array.remove(axis, dimension);
						}

						return axis;
					};

					if (xLayout.columns) {
						xLayout.columns = getUpdatedAxis(xLayout.columns);
					}
					if (xLayout.rows) {
						xLayout.rows = getUpdatedAxis(xLayout.rows);
					}
					if (xLayout.filters) {
						xLayout.filters = getUpdatedAxis(xLayout.filters);
					}
				};

				getHeaderNames = function() {
					var headerNames = [];

					for (var i = 0; i < response.headers.length; i++) {
						headerNames.push(response.headers[i].name);
					}

					return headerNames;
				};

				return function() {
					var headerNames = getHeaderNames(),
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
						co = dimConf.category.objectName,
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
										name: service.layout.getItemName(xLayout, response, init.user.ou, false)
									}];
								}
								if (isUserOrgunitChildren) {
									userOuc = [];

									for (var j = 0; j < init.user.ouc.length; j++) {
										userOuc.push({
											id: init.user.ouc[j],
											name: service.layout.getItemName(xLayout, response, init.user.ouc[j], false)
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
												name: service.layout.getItemName(xLayout, response, id, false)
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
										name: service.layout.getItemName(xLayout, response, id, false)
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

					// Remove dimensions from layout that do not exist in response
					for (var i = 0, dimensionName; i < xLayout.axisDimensionNames.length; i++) {
						dimensionName = xLayout.axisDimensionNames[i];
						if (!Ext.Array.contains(headerNames, dimensionName)) {
							removeDimensionFromXLayout(dimensionName);
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
				}();
			};

			service.layout.getExtendedAxis = function(xLayout, xResponse, type) {
				var dimensionNames,
					spanType,
					aDimensions = [],
					nAxisWidth = 1,
					nAxisHeight,
					aaUniqueFloorIds,
					aUniqueFloorWidth = [],
					aAccFloorWidth = [],
					aFloorSpan = [],
					aaGuiFloorIds = [],
					aaAllFloorIds = [],
					aCondoId = [],
					aaAllFloorObjects = [],
					uuidObjectMap = {};

				if (type === 'col') {
					dimensionNames = Ext.clone(xLayout.columnDimensionNames);
					spanType = 'colSpan';
				}
				else if (type === 'row') {
					dimensionNames = Ext.clone(xLayout.rowDimensionNames);
					spanType = 'rowSpan';
				}

				if (!(Ext.isArray(dimensionNames) && dimensionNames.length)) {
					return;
				}
	//dimensionNames = ['pe', 'ou'];

				// aDimensions: array of dimension objects with dimensionName property
				for (var i = 0; i < dimensionNames.length; i++) {
					aDimensions.push({
						dimensionName: dimensionNames[i]
					});
				}
	//aDimensions = [{
		//dimensionName: 'pe'
	//}]

				// aaUniqueFloorIds: array of arrays with unique ids for each dimension floor
				aaUniqueFloorIds = function() {
					var a = [];

					for (var i = 0; i < aDimensions.length; i++) {
						a.push(xResponse.nameHeaderMap[aDimensions[i].dimensionName].ids);
					}

					return a;
				}();
	//aaUniqueFloorIds	= [ [de-id1, de-id2, de-id3],
	//					    [pe-id1],
	//					    [ou-id1, ou-id2, ou-id3, ou-id4] ]


				// nAxisHeight
				nAxisHeight = aaUniqueFloorIds.length;
	//nAxisHeight = 3


				// aUniqueFloorWidth, nAxisWidth, aAccFloorWidth
				for (var i = 0, nUniqueFloorWidth; i < nAxisHeight; i++) {
					nUniqueFloorWidth = aaUniqueFloorIds[i].length;

					aUniqueFloorWidth.push(nUniqueFloorWidth);
					nAxisWidth = nAxisWidth * nUniqueFloorWidth;
					aAccFloorWidth.push(nAxisWidth);
				}
	//aUniqueFloorWidth	= [3, 1, 4]
	//nAxisWidth		= 12 (3 * 1 * 4)
	//aAccFloorWidth	= [3, 3, 12]

				// aFloorSpan
				for (var i = 0; i < nAxisHeight; i++) {
					if (aUniqueFloorWidth[i] === 1) {
						if (i === 0) { // if top floor
							aFloorSpan.push(nAxisWidth); // span max
						}
						else {
							if (xLayout.hideEmptyRows && type === 'row') {
								aFloorSpan.push(nAxisWidth / aAccFloorWidth[i]);
							}
							else {
								aFloorSpan.push(aFloorSpan[0]); //if just one item and not top level, span same as top level
							}
						}
					}
					else {
						aFloorSpan.push(nAxisWidth / aAccFloorWidth[i]);
					}
				}
	//aFloorSpan			= [4, 12, 1]


				// aaGuiFloorIds
				aaGuiFloorIds.push(aaUniqueFloorIds[0]);

				if (nAxisHeight.length > 1) {
					for (var i = 1, a, n; i < nAxisHeight; i++) {
						a = [];
						n = aUniqueFloorWidth[i] === 1 ? aUniqueFloorWidth[0] : aAccFloorWidth[i-1];

						for (var j = 0; j < n; j++) {
							a = a.concat(aaUniqueFloorIds[i]);
						}

						aaGuiFloorIds.push(a);
					}
				}
	//aaGuiFloorIds	= [ [d1, d2, d3], (3)
	//					[p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5], (15)
	//					[o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2...] (30)
	//		  	  	  ]


				// aaAllFloorIds
				for (var i = 0, aAllFloorIds, aUniqueFloorIds, span, factor; i < nAxisHeight; i++) {
					aAllFloorIds = [];
					aUniqueFloorIds = aaUniqueFloorIds[i];
					span = aFloorSpan[i];
					factor = nAxisWidth / (span * aUniqueFloorIds.length);

					for (var j = 0; j < factor; j++) {
						for (var k = 0; k < aUniqueFloorIds.length; k++) {
							for (var l = 0; l < span; l++) {
								aAllFloorIds.push(aUniqueFloorIds[k]);
							}
						}
					}

					aaAllFloorIds.push(aAllFloorIds);
				}
	//aaAllFloorIds	= [ [d1, d1, d1, d1, d1, d1, d1, d1, d1, d1, d2, d2, d2, d2, d2, d2, d2, d2, d2, d2, d3, d3, d3, d3, d3, d3, d3, d3, d3, d3], (30)
	//					[p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5], (30)
	//					[o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2] (30)
	//		  	  	  ]


				// aCondoId
				for (var i = 0, id; i < nAxisWidth; i++) {
					id = '';

					for (var j = 0; j < nAxisHeight; j++) {
						id += aaAllFloorIds[j][i];
					}

					if (id) {
						aCondoId.push(id);
					}
				}
	//aCondoId	= [ id11+id21+id31, id12+id22+id32, ... ]


				// allObjects
				for (var i = 0, allFloor; i < aaAllFloorIds.length; i++) {
					allFloor = [];

					for (var j = 0, obj; j < aaAllFloorIds[i].length; j++) {
						obj = {
							id: aaAllFloorIds[i][j],
							uuid: Ext.data.IdGenerator.get('uuid').generate(),
							dim: i,
							axis: type
						};

						// leaf?
						if (i === aaAllFloorIds.length - 1) {
							obj.leaf = true;
						}

						allFloor.push(obj);
					}

					aaAllFloorObjects.push(allFloor);
				}

				// add span and children
				for (var i = 0; i < aaAllFloorObjects.length; i++) {
					for (var j = 0, obj, doorCount = 0, oldestObj; j < aaAllFloorObjects[i].length; j++) {

						obj = aaAllFloorObjects[i][j];

						if (doorCount === 0) {

							// span
							obj[spanType] = aFloorSpan[i];

							// children
							//obj.children = Ext.isDefined(aFloorSpan[i + 1]) ? aFloorSpan[i] / aFloorSpan[i + 1] : 0;
							obj.children = obj.leaf ? 0 : aFloorSpan[i];

							// first sibling
							obj.oldest = true;

							// root?
							if (i === 0) {
								obj.root = true;
							}

							// tmp oldest uuid
							oldestObj = obj;
						}

						obj.oldestSibling = oldestObj;

						if (++doorCount === aFloorSpan[i]) {
							doorCount = 0;
						}
					}
				}

				// add parents if more than 1 floor
				if (nAxisHeight > 1) {
					for (var i = 1, allFloor; i < nAxisHeight; i++) {
						allFloor = aaAllFloorObjects[i];

						//for (var j = 0, obj, doorCount = 0, span = aFloorSpan[i - 1], parentObj = aaAllFloorObjects[i - 1][0]; j < allFloor.length; j++) {
						for (var j = 0, doorCount = 0, span = aFloorSpan[i - 1]; j < allFloor.length; j++) {
							allFloor[j].parent = aaAllFloorObjects[i - 1][j];

							//doorCount++;

							//if (doorCount === span) {
								//parentObj = aaAllFloorObjects[i - 1][j + 1];
								//doorCount = 0;
							//}
						}
					}
				}

				// add uuids array to leaves
				if (aaAllFloorObjects.length) {

					// set span to second lowest span number: if aFloorSpan == [15,3,15,1], set span to 3
					var span = nAxisHeight > 1 ? support.prototype.array.sort(Ext.clone(aFloorSpan))[1] : nAxisWidth,
						allFloorObjectsLast = aaAllFloorObjects[aaAllFloorObjects.length - 1];

					for (var i = 0, leaf, parentUuids, obj, leafUuids = []; i < allFloorObjectsLast.length; i++) {
						leaf = allFloorObjectsLast[i];
						leafUuids.push(leaf.uuid);
						parentUuids = [];
						obj = leaf;

						// get the uuid of the oldest sibling
						while (obj.parent) {
							obj = obj.parent;
							parentUuids.push(obj.oldestSibling.uuid);
						}

						// add parent uuids to leaf
						leaf.uuids = Ext.clone(parentUuids);

						// add uuid for all leaves
						if (leafUuids.length === span) {
							for (var j = (i - span) + 1, leaf; j <= i; j++) {
								leaf = allFloorObjectsLast[j];
								leaf.uuids = leaf.uuids.concat(Ext.clone(leafUuids));
							}

							leafUuids = [];
						}
					}
				}

				// populate uuidObject map
				for (var i = 0; i < aaAllFloorObjects.length; i++) {
					for (var j = 0, object; j < aaAllFloorObjects[i].length; j++) {
						object = aaAllFloorObjects[i][j];
//console.log(object.uuid, object);
						uuidObjectMap[object.uuid] = object;
					}
				}

//console.log("aaAllFloorObjects", aaAllFloorObjects);

				return {
					type: type,
					items: aDimensions,
					xItems: {
						unique: aaUniqueFloorIds,
						gui: aaGuiFloorIds,
						all: aaAllFloorIds
					},
					objects: {
						all: aaAllFloorObjects
					},
					ids: aCondoId,
					span: aFloorSpan,
					dims: nAxisHeight,
					size: nAxisWidth,
					uuidObjectMap: uuidObjectMap
				};
			};

			service.layout.isHierarchy = function(layout, response, id) {
				return layout.showHierarchy && Ext.isObject(response.metaData.ouHierarchy) && response.metaData.ouHierarchy.hasOwnProperty(id);
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

				if (layout.showTotals) {
					delete layout.showTotals;
				}

				if (layout.showSubTotals) {
					delete layout.showSubTotals;
				}

				if (!layout.hideEmptyRows) {
					delete layout.hideEmptyRows;
				}

				if (!layout.showHierarchy) {
					delete layout.showHierarchy;
				}

				if (layout.displayDensity === 'normal') {
					delete layout.displayDensity;
				}

				if (layout.fontSize === 'normal') {
					delete layout.fontSize;
				}

				if (layout.digitGroupSeparator === 'space') {
					delete layout.digitGroupSeparator;
				}

				if (!layout.legendSet) {
					delete layout.legendSet;
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

				if (Ext.isObject(component.mask) && component.mask.destroy) {
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
					dx = dimConf.indicator.dimensionName,
					co = dimConf.category.dimensionName;

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

					if (dimName !== co) {
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

				if (xLayout.showHierarchy) {
					paramString += '&hierarchyMeta=true';
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

			// pivot
			web.pivot = {};

			web.pivot.getHtml = function(xLayout, xResponse, xColAxis, xRowAxis) {
				var getRoundedHtmlValue,
					getTdHtml,
					doSubTotals,
					doTotals,
					getColAxisHtmlArray,
					getRowHtmlArray,
					rowAxisHtmlArray,
					getColTotalHtmlArray,
					getGrandTotalHtmlArray,
					getTotalHtmlArray,
					getHtml,
					getUniqueFactor = function(xAxis) {
						if (!xAxis) {
							return null;
						}

						var unique = xAxis.xItems.unique;

						if (unique) {
							return unique.length < 2 ? 1 : (xAxis.size / unique[0].length);
						}

						return null;
					},
					colUniqueFactor = getUniqueFactor(xColAxis),
					rowUniqueFactor = getUniqueFactor(xRowAxis),
					valueItems = [],
					valueObjects = [],
					totalColObjects = [],
					uuidDimUuidsMap = {},
					isLegendSet = Ext.isObject(xLayout.legendSet) && Ext.isArray(xLayout.legendSet.mapLegends) && xLayout.legendSet.mapLegends.length,
					htmlArray;

				getRoundedHtmlValue = function(value, dec) {
					dec = dec || 2;
					return parseFloat(support.prototype.number.roundIf(value, 2)).toString();
				};

				getTdHtml = function(config, metaDataId) {
					var bgColor,
						mapLegends,
						colSpan,
						rowSpan,
						htmlValue,
						displayDensity,
						fontSize,
						isNumeric = Ext.isObject(config) && Ext.isString(config.type) && config.type.substr(0,5) === 'value' && !config.empty,
						isValue = Ext.isObject(config) && Ext.isString(config.type) && config.type === 'value' && !config.empty,
						cls = '',
						html = '';

					if (!Ext.isObject(config)) {
						return '';
					}

					// Background color from legend set
					if (isNumeric && xLayout.legendSet) {
						var value = parseFloat(config.value);
						mapLegends = xLayout.legendSet.mapLegends;

						for (var i = 0; i < mapLegends.length; i++) {
							if (Ext.Number.constrain(value, mapLegends[i].startValue, mapLegends[i].endValue) === value) {
								bgColor = mapLegends[i].color;
							}
						}
					}

					colSpan = config.colSpan ? 'colspan="' + config.colSpan + '" ' : '';
					rowSpan = config.rowSpan ? 'rowspan="' + config.rowSpan + '" ' : '';
					htmlValue = config.collapsed ? '' : config.htmlValue || config.value || '';
					htmlValue = config.type !== 'dimension' ? support.prototype.number.prettyPrint(htmlValue, xLayout.digitGroupSeparator) : htmlValue;
					displayDensity = conf.pivot.displayDensity[config.displayDensity] || conf.pivot.displayDensity[xLayout.displayDensity];
					fontSize = conf.pivot.fontSize[config.fontSize] || conf.pivot.fontSize[xLayout.fontSize];

					cls += config.hidden ? ' td-hidden' : '';
					cls += config.collapsed ? ' td-collapsed' : '';
					cls += isValue ? ' pointer' : '';
					cls += bgColor ? ' legend' : (config.cls ? ' ' + config.cls : '');

					// sorting
					if (Ext.isString(metaDataId)) {
						cls += ' td-sortable';

						xLayout.sortableIdObjects.push({
							id: metaDataId,
							uuid: config.uuid
						});
					}

					html += '<td ' + (config.uuid ? ('id="' + config.uuid + '" ') : '');
					html += ' class="' + cls + '" ' + colSpan + rowSpan


					if (bgColor) {
						html += '>';
						html += '<div class="legendCt">';
						html += '<div class="number ' + config.cls + '" style="padding:' + displayDensity + '; padding-right:3px; font-size:' + fontSize + '">' + htmlValue + '</div>';
						html += '<div class="arrowCt ' + config.cls + '">';
						html += '<div class="arrow" style="border-bottom:8px solid transparent; border-right:8px solid ' + bgColor + '">&nbsp;</div>';
						html += '</div></div></div></td>';

						//cls = 'legend';
						//cls += config.hidden ? ' td-hidden' : '';
						//cls += config.collapsed ? ' td-collapsed' : '';

						//html += '<td class="' + cls + '" ';
						//html += colSpan + rowSpan + '>';
						//html += '<div class="legendCt">';
						//html += '<div style="display:table-cell; padding:' + displayDensity + '; font-size:' + fontSize + '"';
						//html += config.cls ? ' class="' + config.cls + '">' : '';
						//html += htmlValue + '</div>';
						//html += '<div class="legendColor" style="background-color:' + bgColor + '">&nbsp;</div>';
						//html += '</div></td>';
					}
					else {
						html += 'style="padding:' + displayDensity + '; font-size:' + fontSize + ';"' + '>' + htmlValue + '</td>';
					}

					return html;
				};

				doSubTotals = function(xAxis) {
					return !!xLayout.showSubTotals && xAxis && xAxis.dims > 1;

					//var multiItemDimension = 0,
						//unique;

					//if (!(xLayout.showSubTotals && xAxis && xAxis.dims > 1)) {
						//return false;
					//}

					//unique = xAxis.xItems.unique;

					//for (var i = 0; i < unique.length; i++) {
						//if (unique[i].length > 1) {
							//multiItemDimension++;
						//}
					//}

					//return (multiItemDimension > 1);
				};

				doTotals = function() {
					return !!xLayout.showTotals;
				};

				doSortableColumnHeaders = function() {
					return (xRowAxis && xRowAxis.dims === 1);
				};

				getColAxisHtmlArray = function() {
					var a = [],
						getEmptyHtmlArray;

					getEmptyHtmlArray = function() {
						return (xColAxis && xRowAxis) ? getTdHtml({
							cls: 'pivot-dim-empty cursor-default',
							colSpan: xRowAxis.dims,
							rowSpan: xColAxis.dims,
							htmlValue: '&nbsp;'
						}) : '';
					};

					if (!(xColAxis && Ext.isObject(xColAxis))) {
						return a;
					}

					// for each col dimension
					for (var i = 0, dimHtml; i < xColAxis.dims; i++) {
						dimHtml = [];

						if (i === 0) {
							dimHtml.push(getEmptyHtmlArray());
						}

						for (var j = 0, obj, spanCount = 0, condoId, totalId; j < xColAxis.size; j++) {
							spanCount++;

							obj = xColAxis.objects.all[i][j];
							obj.type = 'dimension';
							obj.cls = 'pivot-dim';
							obj.noBreak = false;
							obj.hidden = !(obj.rowSpan || obj.colSpan);
							obj.htmlValue = service.layout.getItemName(xLayout, xResponse, obj.id, true);

							// sortable column headers. last dim only.
							if (i === xColAxis.dims - 1 && doSortableColumnHeaders()) {
								condoId = xColAxis.ids[j].split('-').join('');
							}

							dimHtml.push(getTdHtml(obj, condoId));

							if (i === 0 && spanCount === xColAxis.span[i] && doSubTotals(xColAxis) ) {
								dimHtml.push(getTdHtml({
									type: 'dimensionSubtotal',
									cls: 'pivot-dim-subtotal cursor-default',
									rowSpan: xColAxis.dims,
									htmlValue: '&nbsp;'
								}));

								spanCount = 0;
							}

							if (i === 0 && (j === xColAxis.size - 1) && doTotals()) {
								totalId = doSortableColumnHeaders() ? 'total_' : null;

								dimHtml.push(getTdHtml({
									uuid: Ext.data.IdGenerator.get('uuid').generate(),
									type: 'dimensionTotal',
									cls: 'pivot-dim-total',
									rowSpan: xColAxis.dims,
									htmlValue: 'Total'
								}, totalId));
							}
						}

						a.push(dimHtml);
					}

					return a;
				};

				getRowHtmlArray = function() {
					var a = [],
						axisAllObjects = [],
						xValueObjects,
						totalValueObjects = [],
						mergedObjects = [],
						valueItemsCopy,
						colAxisSize = xColAxis ? xColAxis.size : 1,
						rowAxisSize = xRowAxis ? xRowAxis.size : 1,
						recursiveReduce;

					recursiveReduce = function(obj) {
						if (!obj.children) {
							obj.collapsed = true;

							if (obj.parent) {
								obj.parent.oldestSibling.children--;
							}
						}

						if (obj.parent) {
							recursiveReduce(obj.parent.oldestSibling);
						}
					};

					// dimension
					if (xRowAxis) {
						for (var i = 0, row; i < xRowAxis.size; i++) {
							row = [];

							for (var j = 0, obj, newObj; j < xRowAxis.dims; j++) {
								obj = xRowAxis.objects.all[j][i];
								obj.type = 'dimension';
								obj.cls = 'pivot-dim td-nobreak' + (service.layout.isHierarchy(xLayout, xResponse, obj.id) ? ' align-left' : '');
								obj.noBreak = true;
								obj.hidden = !(obj.rowSpan || obj.colSpan);
								obj.htmlValue = service.layout.getItemName(xLayout, xResponse, obj.id, true);

								row.push(obj);
							}

							axisAllObjects.push(row);
						}
					}
	//axisAllObjects = [ [ dim, dim ]
	//				     [ dim, dim ]
	//				     [ dim, dim ]
	//				     [ dim, dim ] ];

					// value
					for (var i = 0, valueItemsRow, valueObjectsRow, idValueMap = Ext.clone(xResponse.idValueMap); i < rowAxisSize; i++) {
						valueItemsRow = [];
						valueObjectsRow = [];

						for (var j = 0, id, value, htmlValue, empty, uuid, uuids; j < colAxisSize; j++) {
							empty = false;
							uuids = [];

							// meta data uid
							id = (xColAxis ? support.prototype.str.replaceAll(xColAxis.ids[j], '-', '') : '') + (xRowAxis ? support.prototype.str.replaceAll(xRowAxis.ids[i], '-', '') : '');

							// value html element id
							uuid = Ext.data.IdGenerator.get('uuid').generate();

							// get uuids array from colaxis/rowaxis leaf
							if (xColAxis) {
								uuids = uuids.concat(xColAxis.objects.all[xColAxis.dims - 1][j].uuids);
							}
							if (xRowAxis) {
								uuids = uuids.concat(xRowAxis.objects.all[xRowAxis.dims - 1][i].uuids);
							}

							if (idValueMap[id]) {
								value = parseFloat(idValueMap[id]);
								htmlValue = value.toString();
							}
							else {
								value = 0;
								htmlValue = '&nbsp;';
								empty = true;
							}

							valueItemsRow.push(value);
							valueObjectsRow.push({
								uuid: uuid,
								type: 'value',
								cls: 'pivot-value' + (empty ? ' cursor-default' : ''),
								value: value,
								htmlValue: htmlValue,
								empty: empty,
								uuids: uuids
							});

							// map element id to dim element ids
							uuidDimUuidsMap[uuid] = uuids;
						}

						valueItems.push(valueItemsRow);
						valueObjects.push(valueObjectsRow);
					}

					// totals
					if (xColAxis && doTotals()) {
						for (var i = 0, empty = [], total = 0; i < valueObjects.length; i++) {
							for (j = 0, obj; j < valueObjects[i].length; j++) {
								obj = valueObjects[i][j];

								empty.push(obj.empty);
								total += obj.value;
							}

							// row totals
							totalValueObjects.push({
								type: 'valueTotal',
								cls: 'pivot-value-total',
								value: total,
								htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(total) : '',
								empty: !Ext.Array.contains(empty, false)
							});

							// add row totals to idValueMap to make sorting on totals possible
							if (doSortableColumnHeaders()) {
								var totalId = 'total_' + xRowAxis.ids[i],
									isEmpty = !Ext.Array.contains(empty, false);

								xResponse.idValueMap[totalId] = isEmpty ? null : total;
							}

							empty = [];
							total = 0;
						}
					}

					// hide empty rows (dims/values/totals)
					if (xColAxis && xRowAxis) {
						if (xLayout.hideEmptyRows) {
							for (var i = 0, valueRow, isValueRowEmpty, dimLeaf; i < valueObjects.length; i++) {
								valueRow = valueObjects[i];
								isValueRowEmpty = !Ext.Array.contains(Ext.Array.pluck(valueRow, 'empty'), false);

								// if value row is empty
								if (isValueRowEmpty) {

									// Hide values by adding collapsed = true to all items
									for (var j = 0; j < valueRow.length; j++) {
										valueRow[j].collapsed = true;
									}

									// Hide totals by adding collapsed = true to all items
									if (doTotals()) {
										totalValueObjects[i].collapsed = true;
									}

									// Hide/reduce parent dim span
									dimLeaf = axisAllObjects[i][xRowAxis.dims-1];
									recursiveReduce(dimLeaf);
								}
							}
						}
					}

					xValueObjects = Ext.clone(valueObjects);

					// col subtotals
					if (doSubTotals(xColAxis)) {
						var tmpValueObjects = [];

						for (var i = 0, row, rowSubTotal, colCount; i < xValueObjects.length; i++) {
							row = [];
							rowSubTotal = 0;
							colCount = 0;

							for (var j = 0, item, collapsed = [], empty = []; j < xValueObjects[i].length; j++) {
								item = xValueObjects[i][j];
								rowSubTotal += item.value;
								empty.push(!!item.empty);
								collapsed.push(!!item.collapsed);
								colCount++;

								row.push(item);

								if (colCount === colUniqueFactor) {
									var isEmpty = !Ext.Array.contains(empty, false);
									row.push({
										type: 'valueSubtotal',
										cls: 'pivot-value-subtotal' + (isEmpty ? ' cursor-default' : ''),
										value: rowSubTotal,
										htmlValue: isEmpty ? '&nbsp;' : getRoundedHtmlValue(rowSubTotal),
										empty: isEmpty,
										collapsed: !Ext.Array.contains(collapsed, false)
									});

									colCount = 0;
									rowSubTotal = 0;
									empty = [];
									collapsed = [];
								}
							}

							tmpValueObjects.push(row);
						}

						xValueObjects = tmpValueObjects;
					}

					// row subtotals
					if (doSubTotals(xRowAxis)) {
						var tmpAxisAllObjects = [],
							tmpValueObjects = [],
							tmpTotalValueObjects = [],
							getAxisSubTotalRow;

						getAxisSubTotalRow = function(collapsed) {
							var row = [];

							for (var i = 0, obj; i < xRowAxis.dims; i++) {
								obj = {};
								obj.type = 'dimensionSubtotal';
								obj.cls = 'pivot-dim-subtotal cursor-default';
								obj.collapsed = Ext.Array.contains(collapsed, true);

								if (i === 0) {
									obj.htmlValue = '&nbsp;';
									obj.colSpan = xRowAxis.dims;
								}
								else {
									obj.hidden = true;
								}

								row.push(obj);
							}

							return row;
						};

						// tmpAxisAllObjects
						for (var i = 0, row, collapsed = []; i < axisAllObjects.length; i++) {
							tmpAxisAllObjects.push(axisAllObjects[i]);
							collapsed.push(!!axisAllObjects[i][0].collapsed);

							// Insert subtotal after last objects
							if (!Ext.isArray(axisAllObjects[i+1]) || !!axisAllObjects[i+1][0].root) {
								tmpAxisAllObjects.push(getAxisSubTotalRow(collapsed));

								collapsed = [];
							}
						}

						// tmpValueObjects
						for (var i = 0; i < tmpAxisAllObjects.length; i++) {
							tmpValueObjects.push([]);
						}

						for (var i = 0; i < xValueObjects[0].length; i++) {
							for (var j = 0, rowCount = 0, tmpCount = 0, subTotal = 0, empty = [], collapsed, item; j < xValueObjects.length; j++) {
								item = xValueObjects[j][i];
								tmpValueObjects[tmpCount++].push(item);
								subTotal += item.value;
								empty.push(!!item.empty);
								rowCount++;

								if (axisAllObjects[j][0].root) {
									collapsed = !!axisAllObjects[j][0].collapsed;
								}

								if (!Ext.isArray(axisAllObjects[j+1]) || axisAllObjects[j+1][0].root) {
									var isEmpty = !Ext.Array.contains(empty, false);

									tmpValueObjects[tmpCount++].push({
										type: item.type === 'value' ? 'valueSubtotal' : 'valueSubtotalTotal',
										value: subTotal,
										htmlValue: isEmpty ? '&nbsp;' : getRoundedHtmlValue(subTotal),
										collapsed: collapsed,
										cls: (item.type === 'value' ? 'pivot-value-subtotal' : 'pivot-value-subtotal-total') + (isEmpty ? ' cursor-default' : '')
									});
									rowCount = 0;
									subTotal = 0;
									empty = [];
								}
							}
						}

						// tmpTotalValueObjects
						for (var i = 0, obj, collapsed = [], empty = [], subTotal = 0, count = 0; i < totalValueObjects.length; i++) {
							obj = totalValueObjects[i];
							tmpTotalValueObjects.push(obj);

							collapsed.push(!!obj.collapsed);
							empty.push(!!obj.empty);
							subTotal += obj.value;
							count++;

							if (count === xRowAxis.span[0]) {
								var isEmpty = !Ext.Array.contains(empty, false);

								tmpTotalValueObjects.push({
									type: 'valueTotalSubgrandtotal',
									cls: 'pivot-value-total-subgrandtotal' + (isEmpty ? ' cursor-default' : ''),
									value: subTotal,
									htmlValue: isEmpty ? '&nbsp;' : getRoundedHtmlValue(subTotal),
									empty: isEmpty,
									collapsed: !Ext.Array.contains(collapsed, false)
								});

								collapsed = [];
								empty = [];
								subTotal = 0;
								count = 0;
							}
						}

						axisAllObjects = tmpAxisAllObjects;
						xValueObjects = tmpValueObjects;
						totalValueObjects = tmpTotalValueObjects;
					}

					// Merge dim, value, total
					for (var i = 0, row; i < xValueObjects.length; i++) {
						row = [];

						if (xRowAxis) {
							row = row.concat(axisAllObjects[i]);
						}

						row = row.concat(xValueObjects[i]);

						if (xColAxis) {
							row = row.concat(totalValueObjects[i]);
						}

						mergedObjects.push(row);
					}

					// Create html items
					for (var i = 0, row; i < mergedObjects.length; i++) {
						row = [];

						for (var j = 0; j < mergedObjects[i].length; j++) {
							row.push(getTdHtml(mergedObjects[i][j]));
						}

						a.push(row);
					}

					return a;
				};

				getColTotalHtmlArray = function() {
					var a = [];

					if (xRowAxis && doTotals()) {
						var xTotalColObjects;

						// Total col items
						for (var i = 0, total = 0, empty = []; i < valueObjects[0].length; i++) {
							for (var j = 0, obj; j < valueObjects.length; j++) {
								obj = valueObjects[j][i];

								total += obj.value;
								empty.push(!!obj.empty);
							}

							// col total
							totalColObjects.push({
								type: 'valueTotal',
								value: total,
								htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(total) : '',
								empty: !Ext.Array.contains(empty, false),
								cls: 'pivot-value-total'
							});

							total = 0;
							empty = [];
						}

						xTotalColObjects = Ext.clone(totalColObjects);

						if (xColAxis && doSubTotals(xColAxis)) {
							var tmp = [];

							for (var i = 0, item, subTotal = 0, empty = [], colCount = 0; i < xTotalColObjects.length; i++) {
								item = xTotalColObjects[i];
								tmp.push(item);
								subTotal += item.value;
								empty.push(!!item.empty);
								colCount++;

								if (colCount === colUniqueFactor) {
									tmp.push({
										type: 'valueTotalSubgrandtotal',
										value: subTotal,
										htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(subTotal) : '',
										empty: !Ext.Array.contains(empty, false),
										cls: 'pivot-value-total-subgrandtotal'
									});

									subTotal = 0;
									colCount = 0;
								}
							}

							xTotalColObjects = tmp;
						}

						// Total col html items
						for (var i = 0; i < xTotalColObjects.length; i++) {
							a.push(getTdHtml(xTotalColObjects[i]));
						}
					}

					return a;
				};

				getGrandTotalHtmlArray = function() {
					var total = 0,
						empty = [],
						a = [];

					if (doTotals()) {
						for (var i = 0, obj; i < totalColObjects.length; i++) {
							obj = totalColObjects[i];

							total += obj.value;
							empty.push(obj.empty);
						}

						if (xColAxis && xRowAxis) {
							a.push(getTdHtml({
								type: 'valueGrandTotal',
								cls: 'pivot-value-grandtotal',
								value: total,
								htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(total) : '',
								empty: !Ext.Array.contains(empty, false)
							}));
						}
					}

					return a;
				};

				getTotalHtmlArray = function() {
					var dimTotalArray,
						colTotal = getColTotalHtmlArray(),
						grandTotal = getGrandTotalHtmlArray(),
						row,
						a = [];

					if (doTotals()) {
						if (xRowAxis) {
							dimTotalArray = [getTdHtml({
								type: 'dimensionSubtotal',
								cls: 'pivot-dim-total',
								colSpan: xRowAxis.dims,
								htmlValue: 'Total'
							})];
						}

						row = [].concat(dimTotalArray || [], Ext.clone(colTotal) || [], Ext.clone(grandTotal) || []);

						a.push(row);
					}

					return a;
				};

				getHtml = function() {
					var s = '<table id="' + xLayout.tableUuid + '" class="pivot">';

					for (var i = 0; i < htmlArray.length; i++) {
						s += '<tr>' + htmlArray[i].join('') + '</tr>';
					}

					return s += '</table>';
				};

				// get html
				return function() {
					htmlArray = Ext.Array.clean([].concat(getColAxisHtmlArray() || [], getRowHtmlArray() || [], getTotalHtmlArray() || []));

					return {
						html: getHtml(htmlArray),
						uuidDimUuidsMap: uuidDimUuidsMap
					};
				}();
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

			// legend set map
			init.idLegendSetMap = {};

			for (var i = 0, set; i < init.legendSets.length; i++) {
				set = init.legendSets[i];
				init.idLegendSetMap[set.id] = set;
			}
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

	// PLUGIN

		// css
	css = 'table.pivot { \n font-family: arial,sans-serif,ubuntu,consolas; \n } \n';
	css += '.td-nobreak { \n white-space: nowrap; \n } \n';
	css += '.td-hidden { \n display: none; \n } \n';
	css += '.td-collapsed { \n display: none; \n } \n';
	css += 'table.pivot { \n border-collapse: collapse; \n border-spacing: 0px; \n border: 0 none; \n } \n';
	css += '.pivot td { \n padding: 5px; \n border: \n 1px solid #b2b2b2; \n } \n';
	css += '.pivot-dim { \n background-color: #dae6f8; \n text-align: center; \n } \n';
	css += '.pivot-dim.highlighted { \n	background-color: #c5d8f6; \n } \n';
	css += '.pivot-dim-subtotal { \n background-color: #cad6e8; \n text-align: center; \n } \n';
	css += '.pivot-dim-total { \n background-color: #bac6d8; \n text-align: center; \n } \n';
	css += '.pivot-dim-total.highlighted { \n background-color: #adb8c9; \n } \n';
	css += '.pivot-dim-empty { \n background-color: #dae6f8; \n text-align: center; \n } \n';
	css += '.pivot-value { \n background-color: #fff; \n white-space: nowrap; \n text-align: right; \n } \n';
	css += '.pivot-value-subtotal { \n background-color: #f4f4f4; \n white-space: nowrap; \n text-align: right; \n } \n';
	css += '.pivot-value-subtotal-total { \n background-color: #e7e7e7; \n white-space: nowrap; \n text-align: right; \n } \n';
	css += '.pivot-value-total { \n background-color: #e4e4e4; \n white-space: nowrap; \n text-align: right; \n } \n';
	css += '.pivot-value-total-subgrandtotal { \n background-color: #d8d8d8; \n white-space: nowrap; \n text-align: right; \n } \n';
	css += '.pivot-value-grandtotal { \n background-color: #c8c8c8; \n white-space: nowrap; \n text-align: right; \n } \n';

	css += '.x-mask-msg { \n padding: 0; \n	border: 0 none; \n background-image: none; \n background-color: transparent; \n } \n';
	css += '.x-mask-msg div { \n background-position: 11px center; \n } \n';
	css += '.x-mask-msg .x-mask-loading { \n border: 0 none; \n	background-color: #000; \n color: #fff; \n border-radius: 2px; \n padding: 12px 14px 12px 30px; \n opacity: 0.65; \n } \n';

	css += '.pivot td.legend { \n padding: 0; \n } \n';
	css += '.pivot div.legendCt { \n display: table; \n float: right; \n width: 100%; \n } \n';
	css += '.pivot div.arrowCt { \n display: table-cell; \n vertical-align: top; \n width: 8px; \n } \n';
	css += '.pivot div.arrow { \n width: 0; \n height: 0; \n } \n';
	css += '.pivot div.number { \n display: table-cell; \n } \n',
	css += '.pivot div.legendColor { \n display: table-cell; \n width: 2px; \n } \n';

	css += '.pointer { \n cursor: pointer; \n } \n';
	css += '.td-sortable { \n background-image: url("http://dhis2-cdn.org/v214/plugin/images/arrowupdown.png"); \n background-repeat: no-repeat; \n background-position: right center; \n padding-right: 15px !important; \n } \n';

	Ext.util.CSS.createStyleSheet(css);

	PT.plugin = {};

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
				console.log('Report table configuration is not an object');
				return;
			}

			if (!Ext.isString(config.el)) {
				console.log('No valid element id provided');
				return;
			}

			config.id = config.id || config.uid;

			return true;
		};

        extendInstance = function(pt) {
            var init = ns.core.init,
				api = ns.core.api,
				support = ns.core.support,
				service = ns.core.service,
				web = ns.core.web;

			// mouse events
			web.events = web.events || {};

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

            web.pivot.loadTable = function(id) {
				if (!Ext.isString(id)) {
					alert('Invalid report table id');
					return;
				}

				Ext.data.JsonP.request({
					url: init.contextPath + '/api/reportTables/' + id + '.jsonp?viewClass=dimensional&links=false',
					failure: function(r) {
						window.open(init.contextPath + '/api/reportTables/' + id + '.json?viewClass=dimensional&links=false', '_blank');
					},
					success: function(r) {
						var layout = api.layout.Layout(r);

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
				ns.app.centerRegion.update(config.html);

				// after render
				ns.app.layout = layout;
				ns.app.xLayout = xLayout;
				ns.app.response = response;
				ns.app.xResponse = xResponse;
				ns.app.uuidDimUuidsMap = config.uuidDimUuidsMap;
				ns.app.uuidObjectMap = Ext.applyIf((xColAxis ? xColAxis.uuidObjectMap : {}), (xRowAxis ? xRowAxis.uuidObjectMap : {}));

				// sorting
				web.events.setColumnHeaderMouseHandlers(xLayout, response);

				web.mask.hide(ns.app.centerRegion);
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
		};

		createViewport = function() {
			return {
				centerRegion: Ext.get(config.el)
			};
		};

		initialize = function() {
			if (!validateConfig(config)) {
				return;
			}

			ns.core = PT.getCore(Ext.clone(init));
			extendInstance(ns);

			ns.app.viewport = createViewport();
			ns.app.centerRegion = ns.app.viewport.centerRegion;

			if (config.id) {
				ns.core.web.pivot.loadTable(config.id);
			}
			else {
				layout = ns.core.api.layout.Layout(config);

				if (!layout) {
					return;
				}

				ns.core.web.pivot.getData(layout);
			}
		}();
	};

	PT.plugin.getTable = function(config) {
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
	DHIS.getTable = PT.plugin.getTable;
});
