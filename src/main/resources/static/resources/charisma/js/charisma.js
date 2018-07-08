//disable the ajax cache for ie
$.ajaxSetup({cache:false});
$(document).ready(function () {

	//debugger;
    //themes, change CSS with JS
    //default theme(CSS) is cerulean, change it if needed
    var defaultTheme = 'cerulean';

    var currentTheme = $.cookie('currentTheme') == null ? defaultTheme : $.cookie('currentTheme');
    var msie = navigator.userAgent.match(/msie/i);
    $.browser = {};
    $.browser.msie = {};
//    switchTheme(currentTheme);
    $('#themes i').removeClass('glyphicon glyphicon-ok whitespace').addClass('whitespace');
    $('#themes a[data-value=' + currentTheme + ']').find('i').removeClass('whitespace').addClass('glyphicon glyphicon-ok');

    $('.navbar-toggle').click(function (e) {
        e.preventDefault();
        $('.nav-sm').html($('.navbar-collapse').html());
        $('.sidebar-nav').toggleClass('active');
        $(this).toggleClass('active');
    });

    var $sidebarNav = $('.sidebar-nav');

    // Hide responsive navbar on clicking outside
    $(document).mouseup(function (e) {
        if (!$sidebarNav.is(e.target) // if the target of the click isn't the container...
            && $sidebarNav.has(e.target).length === 0
            && !$('.navbar-toggle').is(e.target)
            && $('.navbar-toggle').has(e.target).length === 0
            && $sidebarNav.hasClass('active')
            )// ... nor a descendant of the container
        {
            e.stopPropagation();
            $('.navbar-toggle').click();
        }
    });


    $('#themes a').click(function (e) {
        e.preventDefault();
        currentTheme = $(this).attr('data-value');
        $.cookie('currentTheme', currentTheme, {expires: 365,path:"/"});
        switchTheme(currentTheme);
    });


    function switchTheme(themeName) {
        if (themeName == 'classic') {
            $('#bs-css').attr('href', contextPath+'css/bootstrap.min.css');
        } else {
            $('#bs-css').attr('href', contextPath+'css/bootstrap-' + themeName + '.min.css');
        }

        $('#themes i').removeClass('glyphicon glyphicon-ok whitespace').addClass('whitespace');
        $('#themes a[data-value=' + themeName + ']').find('i').removeClass('whitespace').addClass('glyphicon glyphicon-ok');
    }




    //establish history variables
    var
        History = window.History, // Note: We are using a capital H instead of a lower h
        State = History.getState(),
        $log = $('#log');

    //bind to State Change
    History.Adapter.bind(window, 'statechange', function () { // Note: We are using statechange instead of popstate
        var State = History.getState(); // Note: We are using History.getState() instead of event.state
        $.ajax({
            url: State.url,
            success: function (msg) {
                // console.log($(msg).find('body').html())
                $('#body').html($(msg).find('#content').parents("#body").html());
                $('#loading').remove();
                //$('#content').fadeIn();
                var newTitle = $(msg).filter('title').text();
                $('title').text(newTitle);
                docReady();
            }
        });
    });



    docReady()
});

function docReady(){

    $('.accordion > a').click(function (e) {
        e.preventDefault();
        var $ul = $(this).siblings('ul');
        $ul.slideToggle();
    });
    $('.btn-minimize').click(function (e) {
        e.preventDefault();
        var $target = $(this).parent().parent().next('.box-content');
        if ($target.is(':visible')) $('i', $(this)).removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
        else                       $('i', $(this)).removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
        $target.slideToggle();
    });

    //ajaxify menus
    // $('a.ajax-link').click(function (e) {
    //     $('.sidebar-nav').removeClass('active');
    //     $('.navbar-toggle').removeClass('active');
    //     $('#loading').remove();
    //     $('#content').parent().append('<div id="loading" class="center">Loading...<div class="center"></div></div>');
    //     var $clink = $(this);
    //     History.pushState(null, null, $clink.attr('href'));
    //     $('ul.main-menu li.active').removeClass('active');
    //     $clink.parent('li').addClass('active');
    //     e.preventDefault();
    //
    // });

    //disallow for save the input data
    $("form").attr("autocomplete","off");

    $(document).ready(function(){
        bindTheMethod();
        if($.fn.select2){
            $("select").select2();
        }


        $(".toggleBox").each(function (i,v){
            $(v).click(function(){
                if($(v).is(":checked"))
                    $(v).parents(".form-group").find("select,input,textarea").attr("disabled","disabled")
                else
                    $(v).parents(".form-group").find("select,input,textarea").removeAttr("disabled")
                $(v).removeAttr("disabled")
            })
        })
        if($.fn.datetimepicker){
            $(".date").datetimepicker({
                language:  'zh-CN',
                weekStart: 1,
                todayBtn:  1,
                autoclose: 1,
                todayHighlight: 1,
                forceParse: 0,
                pickerPosition:"bottom-left"
            });
        }

        //$("textarea[jsontext='true']").jsoneditor();
        if($.fn.tinymce){
            // $("textarea[richtext='true']").tinymce();
            tinymce.init({
                selector: "textarea[richtext='true']",
                language: "zh_CN",
                menubar: false,
                plugins : 'autolink code charmap link image lists elfinder textcolor table media emoticons preview',
                toolbar: [
                    'undo redo | styleselect | bold italic forecolor backcolor | numlist bullist | indent | link media image elfinder | table',
                    'alignleft aligncenter alignright | emoticons charmap | code preview'
                ],
                setup: function(ed) {
                    //check the editor if it is readonly or disabled
                    if ($('#'+ed.id).prop('readonly')||$('#'+ed.id).prop('disabled')) {
                        ed.settings.readonly = true;
                    }
                },
                convert_urls :false

            });
        }
        $("textarea[data-code='true']").each(function(i,v){
            CodeMirror.fromTextArea(v,{
                lineNumbers: true,
                lineWrapping: true,
                mode: "text/javascript"
            })
        })


    })

    $(".elfinder_action").click(function (){
        var container=$(this).parents(".elfinder_container")
        var id=$(this).attr("target");
        var hidden=$("#"+id);


        var temp= $('<div \>');
        temp.dialog({modal: true, width: "80%", title: "选择文件", zIndex: 99999,
            create: function(event, ui) {
                $(this).elfinder({
                    resizable: false,
                    url : rootPath+'/spring/connector',
                    lang:"zh_CN",
                    commandsOptions: {
                        getfile: {
                            oncomplete: 'destroy'
                        }
                    },
                    getFileCallback: function(file) {
                        console.log(file);
                        var data=file.hash+","+file.name;
                        var hash=file.hash;


                        var val=hidden.val();
                        if(val.length>0&&hidden.attr("multiple")=="true")
                            val=val+"|"+data;
                        else
                            val=data;

                        var li="";
                        if(hidden.attr("renderType")=='img')
                            li="<li file=\""+data+"\"><img src=\""+(rootPath+'/spring/connector?cmd=file&target='+hash)+"\"><i class=\"glyphicon glyphicon-remove\" onclick=\"elfinder_delete()\"></i></li>"
                        else
                            li="<li file=\""+data+"\">"+file.name+"<i class=\"glyphicon glyphicon-remove\" onclick=\"elfinder_delete()\"></i></li>";

                        if(hidden.attr("mutiple")=="true"){
                            container.find(".files").append(li);
                        }else{
                            container.find(".files").html(li);
                        }


                        container.find("input[type=hidden]").val(val);


                        jQuery('button.ui-dialog-titlebar-close[role="button"]').click();
                    }
                }).elfinder('instance')
            },
            beforeClose: function(){
                temp.remove()
            }
        });
    })
}


function refreshData(viewName){
    jQuery("#result_"+viewName+" table").bootstrapTable('refresh')
}

function elfinder_delete(){
    loadingMask().showMask();
    var file=$(event.currentTarget).parent().attr("file");
    var node=$(event.currentTarget).parent();
    var container=$(event.currentTarget).parents(".elfinder_container");
    var fieldsName=container.find("input[type=hidden]").attr("name");
    var val="";
    container.find("li").each(function(i,v){
        if($(v).attr("file")!=file)
            val=val+$(v).attr("file")+"|";
    })
    if(val.length>0){
        val=val.substring(0,val.length-1);
    }
    node.remove()
    container.find("input[type=hidden]").val(val);
    loadingMask().hideMask();
}

function doOperate(e){
    var el=$(e.target).closest("a")
    var popup=el.data("popup")||false;
    var id=el.data("id")?"id="+el.data("id"):"";
    var refresh=el.data("refresh");
    var change=el.data("change");
    var conf=el.data("confirm");
    var operate=el.data("operate");
    if(conf){
        if(!confirm(conf)){
            return
        }
    }
    if(popup){
        linkPopup(e)
    }else{
        if(operate){
            $.ajax({
                url: rootPath+"/spring/data/operate/"+el.data("root")+"/"+el.data("name")+"?"+id,
                data: $("form").serialize(),
                method: "post",
                dataType: "json",
                success: function (data) {
                    if(data.status===0){
                        noty({"text":data.msg,"layout":"topCenter","type":"success"})
                        if(refresh){
                            refreshData(refresh);
                        }
                        console.log("change"+change)
                        if(change){
                            $("#"+change).trigger("change")
                        }
                        if(data.redirect){
                            location.href=data.redirectUrl;
                        }
                    }else {
                        noty({"text":data.msg,"layout":"topCenter","type":"error"})
                    }
                },
                fail:function (res) {
                    noty({"text":res.status,"layout":"topCenter","type":"error"})
                }
            })
        }else{
            location.href=rootPath+"/spring/data/"+el.data("type")+"/"+el.data("view")+"?"+id;
        }

    }
}
function doAction(e){
    var el=$(e.target).closest("a")
    var popup=el.data("popup")||false;
    var id=el.data("id")?"id="+el.data("id"):"";
    var refresh=el.data("refresh")
    if(popup){
        linkPopup(e)
    }else{
        $.ajax({
            url: rootPath+"/spring/data/action/"+el.data("root")+"/"+el.data("name")+"?"+id,
            data: $("form").serialize(),
            method: "post",
            dataType: "json",
            success: function (data) {
                if(data.status===0){
                    noty({"text":data.msg,"layout":"topCenter","type":"success"})
                    if(refresh){
                        refreshData(refresh);
                    }
                    if(data.redirect){
                        location.href=data.redirectUrl;
                    }
                }else {
                    noty({"text":data.msg,"layout":"topCenter","type":"error"})
                }
            },
            fail:function (res) {
                noty({"text":res.status,"layout":"topCenter","type":"error"})
            }
        })
    }

}
function linkPopup(e) {
    var el=$(e.target).closest("a")
    var popup=el.data("popup")||false;
    var id=el.data("id")?"&id="+el.data("id"):"";
    var title=el.data("title")||"";
    var temp=$('<div \>');
    temp.dialog({modal: true, width: "80%", title: title, zIndex: 99999,
        create: function(event,ui){

            var parameter=el.data("parameter")?el.data("parameter"):"";
            var html="<iframe frameborder='0' width='100%' height='600'   src='"+rootPath+"/spring/data/"+el.data("type")+"/"+el.data("view")+"?_popup=true"+id+parameter+"'></iframe>";
            $(this).html(html);
        },
        beforeClose: function(){
            var view=el.data("refresh")
            var change=el.data("change");
            if(change){
                $("#"+change).trigger("change")
            }
            if(view){
                refreshData(view);
            }

            temp.remove()
        }
    })
}
function linkGoto(e){
    var el=$(e.target).closest("a")
    var id=el.data("id")?"&id="+el.data("id"):"";
    var parameter=el.data("parameter")?el.data("parameter"):"";
    var url=rootPath+"/spring/data/"+el.data("type")+"/"+el.data("view")+"?"+id+parameter;
    location.href=url

}

function backToView(e){
    var el=$(e.target).closest("a");
    var id=el.data("id")?"&id="+$("#"+el.data("id")).val():"";
    var url=rootPath+"/spring/data/"+el.data("type")+"/"+el.data("view")+"?"+id;
    // var newUrl=location.protocol+"//"+location.host+url;
    console.log(url)
    location.href=url;
}

var loadingMask=function(){
    var loading=$("#loadingMask");
    return {
        showMask:function(){
            loading.modal();
        },
        hideMask:function(){
            loading.modal('hide')
        }
    }
}
function closeDialog(){
    // if(window.parent!=window){
        $(".ui-dialog-content",window.parent.document).dialog("close");
    // }
}

function doExport(event){
    var sortName=null;
    var sortOrder=null;

    var viewname=$(event.target).data("view")
    var selector='#result_'+viewname+' .table';
    if($(selector)&&$(selector).data("bootstrap.table")&&$(selector).data("bootstrap.table").options){
        sortName=$(selector).data("bootstrap.table").options.sortName;
        sortOrder= $(selector).data("bootstrap.table").options.sortOrder;
    }
    if(sortName!=null)
        $(event.target).closest("form").attr("action",rootPath+"/spring/data/export/"+viewname+"?sort="+sortName+"&order="+sortOrder);
    else
        $(event.target).closest("form").attr("action",rootPath+"/spring/data/export/"+viewname);
    $(event.target).closest("form").submit();
}


function popupPicker(e){
    var el=$(e.target);
    var inputEl=$("button[data-picker]").closest(".input-group").find("input")
    var view=el.data("view");
    var title=el.data("title")||"Picker";
    if(view){
        var temp=$('<div \>');
        temp.dialog({modal: true, width: "80%", title: title, zIndex: 99999,
            create: function(event,ui){
                $("#picker_value").val(inputEl.val())
                var html="<iframe frameborder='0' width='100%' height='600'   src='"+rootPath+"/spring/data/search/"+view+"?_popup=true'></iframe>";
                $(this).html(html);
            },
            beforeClose: function(){
                inputEl.val($("#picker_value").val());
            }
        })

    }

}
function popupClear(e){
    var el=$(e.target);
    var inputEl=$("button[data-picker]").closest(".input-group").find("input")
    inputEl.val("");
}
function doPopupClose(){
    if(window.parent!=window){
        var value=window.parent.$("#picker_value").val();
        var test=value?value.split(","):[];
        $(".bootstrap-table tr.selected input").each(function(){
            if(test.indexOf($(this).val())<0){
                test.push($(this).val());
            }
        })
        window.parent.$("#picker_value").val(test.join(","));
        window.parent.closeDialog()
    }
}