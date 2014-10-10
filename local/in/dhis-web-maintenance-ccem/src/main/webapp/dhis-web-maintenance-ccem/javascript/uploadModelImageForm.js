jQuery(document).ready(	function(){
	
	validation( 'uploadModelImageForm', function(form){
		//tempFileName = $('#upload').val();alert( tempFileName );
		form.submit();
	}, function(){
		isSubmit = true;
		
	});
	
	checkValueIsExist( "name", "validateModel.action", {id:getFieldValue('id')});
});


/*
<script type="text/javascript" language="javascript">
$(document).ready(function () {
    $('#formUpload').submit(function (e) {
        var filePath = $('#file').val();
        $.getJSON('@Url.Action("CheckIfFileExists")', { path: filePath },
            function (exists) {
                if (exists) {
                    var cancel = confirm('File "' + filePath + '" has already been uploaded. Overwrite?');
                    if (cancel) {
                        e.preventDefault();
                        return false;
                    }
                }

                return true;
            }
        );
    });
});
</script>
*/