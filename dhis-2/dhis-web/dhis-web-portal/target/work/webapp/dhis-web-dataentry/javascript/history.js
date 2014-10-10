// -----------------------------------------------------------------------------
// Comments
// -----------------------------------------------------------------------------

function saveComment()
{
    var commentValue = $( '#commentTextArea' ).val();

    if ( commentValue.length > 360 )
    {
        markComment( COLOR_YELLOW );
        window.alert(i18n_value_too_long + " for comment field");

        return;
    }

    var commentSaver = new CommentSaver( currentDataElementId, currentOptionComboId, commentValue );

    commentSaver.save();
}

function CommentSaver( de, co, comment )
{
	var pe = $( '#selectedPeriodId' ).val();
	var ou = dhis2.de.currentOrganisationUnitId;
	
	var dataValue = {
	    'de' : de,
	    'co' : co,
	    'ou' : ou,
	    'pe' : pe,
	    'comment' : comment
	};

    var cc = dhis2.de.getCurrentCategoryCombo();
    var cp = dhis2.de.getCurrentCategoryOptionsQueryValue();
    
    if ( cc && cp )
    {
    	dataValue.cc = cc;
    	dataValue.cp = cp;
    }
    
    this.save = function()
    {
        markComment( COLOR_YELLOW );

        $.ajax( {
            url: '../api/dataValues',
            data: dataValue,
            dataType: 'json',
            success: handleSuccess,
            error: handleError
        } );
    };

    function handleSuccess( json )
    {
    	markComment( COLOR_GREEN );
    }

    function handleError( xhr, textStatus, errorThrown )
    {
        markComment( COLOR_RED );
        window.alert( i18n_saving_comment_failed_error_code + '\n\n' + xhr.responseText );
    }
}

function markComment( color )
{
    $( '#commentTextArea' ).css( 'background-color', color );
}

function removeMinMaxLimit()
{
    $( '#minLimit' ).val( '' );
    $( '#maxLimit' ).val( '' );

    $.ajax( {
    	url: 'removeMinMaxLimits.action',
    	data: 
    	{
    		dataElementId: currentDataElementId,
    		categoryOptionComboId: currentOptionComboId,
    		organisationUnitId: dhis2.de.currentOrganisationUnitId
    	},
    	success: function() {    		
    		$( '#minLimit' ).css( 'background-color', COLOR_WHITE );
    		$( '#maxLimit' ).css( 'background-color', COLOR_WHITE );
    		
    		refreshChart()
    	}
    } );
}

function saveMinMaxLimit()
{
    var minValue = $( '#minLimit' ).val();
    var maxValue = $( '#maxLimit' ).val();

    if ( !minValue || minValue == '' )
    {
        return;
    }
    else if ( !isInt( minValue ) )
    {
        $( '#minSpan' ).html( i18n_enter_digits );
        return;
    }
    else
    {
        $( '#minSpan' ).html( '' );
    }

    if ( !maxValue || maxValue == '' )
    {
        return;
    }
    else if ( !isInt( maxValue ) )
    {
        $( '#maxSpan' ).html( i18n_enter_digits );
        return;
    }
    else
    {
        $( '#maxSpan' ).html( '' );
    }

    if ( new Number( minValue ) > new Number( maxValue ) )
    {
        $( '#maxSpan' ).html( i18n_max_must_be_greater_than_min );
        return;
    }
    else
    {
        $( '#maxSpan' ).html( '' );
    }

    var minId = currentDataElementId + '-' + currentOptionComboId + '-min';
    var maxId = currentDataElementId + '-' + currentOptionComboId + '-max';

    dhis2.de.currentMinMaxValueMap[minId] = minValue;
    dhis2.de.currentMinMaxValueMap[maxId] = maxValue;

    $.ajax( {
    	url: 'saveMinMaxLimits.action',
    	data:
    	{
    		organisationUnitId: dhis2.de.currentOrganisationUnitId,
    		dataElementId: currentDataElementId,
    		categoryOptionComboId: currentOptionComboId,
    		minLimit: minValue,
    		maxLimit: maxValue
    	},
    	dataType: 'json',
    	success: function() {
    		$( '#minLimit' ).css( 'background-color', COLOR_GREEN );
    		$( '#maxLimit' ).css( 'background-color', COLOR_GREEN );
    		refreshChart();
    	},
    	error: function() {
    		
    		$( '#minLimit' ).css( 'background-color', COLOR_RED );
    		$( '#maxLimit' ).css( 'background-color', COLOR_RED );
    	}
    } );
}

function refreshChart()
{	
    var periodId = $( '#selectedPeriodId' ).val();
    
    var source = '../api/charts/history/data.png?de=' + currentDataElementId + '&co='
    	+ currentOptionComboId + '&pe=' + periodId + 
    	'&ou=' + dhis2.de.currentOrganisationUnitId + '&r=' + Math.random();

    $( '#historyChart' ).attr( 'src', source );
}

function markValueForFollowup()
{
	var periodId = $( '#selectedPeriodId' ).val();
	
	var dataValue = {
	    'de' : currentDataElementId,
	    'co' : currentOptionComboId,
	    'ou' : dhis2.de.currentOrganisationUnitId,
	    'pe' : periodId,
	    'followUp' : true
	};

    var cc = dhis2.de.getCurrentCategoryCombo();
    var cp = dhis2.de.getCurrentCategoryOptionsQueryValue();
    
    if ( cc && cp )
    {
    	dataValue.cc = cc;
    	dataValue.cp = cp;
    }
    
    $.ajax( { url: '../api/dataValues',
    	data: dataValue,
    	dataType: 'json',
    	success: function( json )
	    {
	        if ( $( '#followup' ).attr( 'src' ) == '../images/unmarked.png' )
	        {
	            $( '#followup' ).attr( 'src', '../images/marked.png' );
	            $( '#followup' ).attr( 'alt', i18n_unmark_value_for_followup );
	        }
	        else
	        {
	            $( '#followup' ).attr( 'src', '../images/unmarked.png' );
	            $( '#followup' ).attr( 'alt', i18n_mark_value_for_followup );
	        }
	    }
	} );
}
