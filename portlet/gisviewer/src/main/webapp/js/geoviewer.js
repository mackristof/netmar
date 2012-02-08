var afterGetFeatureInfo = function(request, event, data) {
    var volet = $('.ui-volet');
    
    if (volet.length > 0) {
        var height = volet.height();
        var len = $('.ui-volet table').length;

        if (len > 0 && height < 25) {
            $('.ui-volet-toggleHandle').click();

        } else if (len == 0 && height > 25) {
            $('.ui-volet-toggleHandle').click();
        }
    }
}

/*** VARIABLE***/

var isDEV = false;
function debug(){
    if(isDEV) {
        console.log.apply(this,arguments);
    }
}

/*************************************************************************/
/** FUNCTIONS ************************************************************/
/*************************************************************************/

function addButtonsEvents(obj) {
    // gestion des boutons
    $(".button:not(.ui-state-disabled)", obj)
    .hover(
        function(){
            $(this).addClass("ui-state-hover");
        },
        function(){
            $(this).removeClass("ui-state-hover");
        }
        )
    .mousedown(
        function(){
            $(this)
            .parents('.buttonSet-single:first')
            .find(".button.ui-state-active")
            .removeClass("ui-state-active");

            if( $(this).is('.ui-state-active.button-toggleable, .buttonSet-multi .ui-state-active') ) {
                $(this).removeClass("ui-state-active");
            }
            else {
                $(this).addClass("ui-state-active");
            }
        }
        )
    .mouseup(
        function(){
            if(! $(this).is('.button-toggleable, .buttonSet-single .button, .buttonSet-multi .button') ) {
                $(this).removeClass("ui-state-active");
            }
        }
        )
    .click(
        function() {
            return false;
        }
        );

}

function clearForm(oForm) {
    $('div.field :input, div.field :selected, div.field :checked', oForm)
    .not(':button, :submit, :reset, :hidden')
    .val('')
    .removeAttr('checked')
    .removeAttr('selected')
    ;

    $('div.checkboxTree').filter('.jstree').jstree('uncheck_all');
};

function getTabPanelsID() {
    var result = new Array();
    /*var oTabPanels = $('.tabPanel');

    oTabPanels.each(function() {
      result.push($(this).attr('id'));
    });*/
    result[0] = "viewerTab"

    return result;
};

function resizeTabPanelLayout() {
    //var tabIndex = oTabs.tabs("option", "selected");
    var tabIndex = 0;
    var oTabPanel = $( "#" + aTabsName[tabIndex] ).show(); // make sure is 'visible'
    var oTabLayout, oSidebarPanel;

    // IF tabLayout exists - get Instance and call resizeAll
    if( oTabPanel.data("layoutContainer") ) {
        // resize the layout-panes - if required
        oTabLayout = oTabPanel.layout();
        oTabLayout.resizeAll();
    }
    // else if tabLayout does not exist yet, create it now and create collapsibles, tree, etc
    else {
        //console.log("create " + aTabsName[tabIndex] + "layout");
        oTabLayout = oTabPanel.layout( tabLayoutOptions );

        // also create other layout
        if(oTabLayout.panes.center.find(".inner-ui-layout-center").length) {
            oTabLayout.panes.center.find(".ui-layout-content").layout( centerLayoutOptions );
        }

        if(oTabLayout.panes.west.find(".inner-ui-layout-center").length) {
            oTabLayout.panes.west.find(".ui-layout-content").layout( sidebarLayoutOptions );
        }

        switch(aTabsName[tabIndex]) {
            /*case "catalogTab" :
          create_collapsible(oTabLayout, "west");
          create_checkboxTree(oTabLayout, "west");
          create_datepicker(oTabLayout, "west");
          break;*/

            case "viewerTab" :
                create_collapsible(oTabLayout, "west");
                //create_tree(oTabLayout,"west");
                //create_moveOneStep(oTabLayout, "west");
                create_volet(oTabLayout, "center");
                //create_couches(oTabLayout, "west");
                break;

        /* case "cartTab" :
          create_collapsible(oTabLayout, "center");
          break;*/
        }

    }

    handle_resizeVolet();

    return;
}

function create_collapsible(tabLayout, where) {
    //debug("create Collapsibles in " + tabLayout.container.attr('id') + " - " + where);

    var oCollapsibles = tabLayout.panes[where].find('div.collapsible');

    if(oCollapsibles.length) {
        oCollapsibles.each(
            function() {
                var $this = $(this);
                if(!$this.hasClass("ui-collapsible")) $this.collapsible();
            }
            );
    }
}


function create_volet(tabLayout, where) {
    debug("create Volets in " + tabLayout.container.attr('id') + " - " + where);

    var oVolet = tabLayout.panes[where].find('div.volet');

    if(oVolet.length) {
        oVolet.each(
            function() {
                var $this = $(this);
                if(!$this.parent().hasClass("ui-volet")) {
                    $this.volet();
                }
            }
            );
    }
}

function handle_resizeVolet(){
    var oVoletBlockContainers = $('div.ui-volet-blockContainer');

    if(oVoletBlockContainers.length) {
        oVoletBlockContainers.each(
            function() {
                var $this = $(this);
                var oParent = $this.parent();
                var oVolet = $this.find('.ui-widget-content');
          
                // Dans  Safari 5 $this.width() retourne le width non redimensionné
                //$this.width((oParent.width() - ($this.outerWidth(true) - $this.width())) + 'px');
                //$this.height((oParent.height() - ($this.outerHeight(true) - $this.height()))+ 'px');
                // $this.width(oParent.width() + 'px');
         
                $this.height((oParent.height() - 20) + 'px');
                oVolet.volet('setVoletSize');
            }
            );
    }
}

function resizeSidebarLayout () {
    //var tabIndex = oTabs.tabs("option", "selected");
    var tabIndex = 0;
    var oTabPanel = $( "#" + aTabsName[tabIndex] ).show(); // make sure is 'visible'
    var oSidebarPanel = $('.ui-layout-west > .ui-layout-content',oTabPanel);
    var oSidebarLayout;

    // IF idebarLayout exists - get Instance and call resizeAll
    if( oSidebarPanel.data("layoutContainer") ) {
        // resize the layout-panes - if required
        oSidebarLayout = oSidebarPanel.layout();
        oSidebarLayout.resizeAll();
    }
    // else if sidebarLayout does not exist yet, create it now
    // pas sûr que ce soit utile puisque déjà créé dans resizeTabPanelLayout !
    //else {
    //  oSidebarLayout = oSidebarPanel.find(".ui-layout-content").layout( sidebarLayoutOptions );
    //}
    
    var legends = jQuery(".layer", jQuery("table[id$='legends_LayerControl']"));
    if(legends.length) {
        legends.each(
            function() {
                var elem = $(this);
                var width = jQuery("#list_legend").width();
                elem.css("width", width - 22);
                elem.css("overflow", "auto");
            }
        );
    }
    
    return;
}

function setCoucheSetterPosition() {
    var oSetterContainer = $('#couche-setter');
    //var oActiveButton = $('.couches li.parameter a.button.ui-state-active');
    var oActiveButtonContainer = $('.couches li.parameter.ui-state-active');

    //if(oActiveButton.length) {
    if(oActiveButtonContainer.length) {
        //var oActiveButtonContainer = oActiveButton.parent();
        var pos = oActiveButtonContainer.offset();
        var ySetter = parseInt(pos.top, 10);
        var xSetter = parseInt(pos.left + oActiveButtonContainer.outerWidth(), 10);

        var oLayoutContent = oActiveButtonContainer.parents('.ui-layout-content');
        var yTopLimit = parseInt(oLayoutContent.offset().top, 10);
        var yBottomLimit = parseInt(yTopLimit + oLayoutContent.outerHeight(), 10);

        if(ySetter < yTopLimit || ySetter > yBottomLimit) {
            oSetterContainer.css('opacity', 0.2);
        }
        else {
            oSetterContainer.css('opacity', 1);
        }

        oSetterContainer.css({
            'top' : ySetter + 'px',
            'left' : xSetter + 'px'
        });
    }

    return false;
}  


/**
 * This function set a new height to the geoviewer portlet body. It 's called on each 
 * resize of the window  to simulate a heigth:100% on portlet-body div
 *
 */
function setDynamicHeightToPortletBody() {   
    var geoviewerIpcStr = "geoviewer_ipc";
    // This boolean defines if the geoviewer_ipc portlet is in the same page of geoviewer portlet
    var hasGeoviewerIpc = false;
    var portletEltsArray = $('#column-1 .portlet-boundary');
    
    for (var i = 0; i <= 1; i++) {
        
        if (portletEltsArray[i] && portletEltsArray[i].id.indexOf(geoviewerIpcStr) != -1) {
            hasGeoviewerIpc = true;
            //            $(portletEltsArray[i]).css("width",0);
            //            $(portletEltsArray[i]).css("height",0);
            $(portletEltsArray[i]).css("margin",0);
            $(portletEltsArray[i]).css("padding",0);
        }
    }
    
    //Set a dynamic height only if the geowiewer is alone (or with the geoviewer_ipc portlet) on the page with a layout 1-column. 
    if ((portletEltsArray.length == 1 || (portletEltsArray.length == 2 && hasGeoviewerIpc))
        && $('#mainContainer').length == 1) {      
	
        /** Firefox **/
        //var portletHeaderHeight = (($('.portlet-topper').length == 0) ? 0 : $('.portlet-topper').outerHeight(true));
        //var footerHeigth = (($('#footer').length == 0) ? 0 : $('#footer').outerHeight(true));    
        //var newHeight = $(window).height() - $('#mainContainer').offset().top - portletHeaderHeight - footerHeigth - 15;

        var portletHeaderHeight = (($('.portlet-topper').length == 0) ? 0 : $('.portlet-topper').outerHeight(true));
        var footerHeigth = (($('#footer').length == 0) ? 0 : $('#footer').outerHeight(true));    
        var newHeight = $(window).height() - $('#mainContainer').offset().top - portletHeaderHeight - footerHeigth - 20;

        if (newHeight < 150) newHeight = 150;

        $('#mainContainer').css("height", parseFloat(newHeight));
    //FOR IE7
    //$('#mainContainer').css("position","relative");
    //$('#viewerTab').css("height", parseFloat(newHeight));    
    //document.getElementById('viewerTab').style.height = parseFloat(newHeight) + "px";        
    }
    
    
}

/********************************************VARIABLES *********************************/

var oTabs = $('#mainContainer');
var aTabsName = getTabPanelsID();

// option layout panel
var tabLayoutOptions = {
    name                : 'tabPanelLayout' // only for debugging
    // required because layout is 'nested' inside tabpanels container
    , 
    resizeWithWindow    : false
    , 
    west__onresize      : function() {
        resizeSidebarLayout();
    }
    , 
    west__size          : "25%"
    , 
    west__minSize       : 350
    , 
    center__onresize    : function() {
        handle_resizeVolet();
    }
    , 
    spacing_open: 11
    , 
    spacing_closed: 11
};

// option si sidebar contient un layout
var sidebarLayoutOptions = {
    name                 : 'sidebarPanelLayout'
    , 
    resizeWithWindow     : false
    , 
    center__paneSelector : ".inner-ui-layout-center"
    , 
    north__paneSelector  : ".inner-ui-layout-north"
    , 
    south__paneSelector  : ".inner-ui-layout-south"
    , 
    east__paneSelector   : ".inner-ui-layout-east"
    , 
    west__paneSelector   : ".inner-ui-layout-west"
    , 
    north__size          : "25%"
    , 
    north__minSize       : "30" /* la taille (en px) du header "Thèmes" + 5px*/
    , 
    south__size          : "25%"
    , 
    closable             : false
    , 
    spacing_open: 11
    , 
    spacing_closed: 11
};

// option si panel "principal" (celui de droite) contient un layout
var centerLayoutOptions = {
    name                 : 'centerPanelLayout'
    , 
    resizeWithWindow     : false
    , 
    center__paneSelector : ".inner-ui-layout-center"
    , 
    north__paneSelector  : ".inner-ui-layout-north"
    , 
    south__paneSelector  : ".inner-ui-layout-south"
    , 
    east__paneSelector   : ".inner-ui-layout-east"
    , 
    west__paneSelector   : ".inner-ui-layout-west"
    , 
    spacing_open: 11
    , 
    spacing_closed: 11
};

// création du layout "externe" contenant le header + tabs
/*var oOuterLayout = $('body').layout({
      name                 : 'outerLayout'
    , center__paneSelector : "#mainContainer"
    , north__resizable     : false
    , north__slidable      : false
    , north__closable      : false
  });*/



var closeWmsPopup = function() {
    $("div[id*='wmsPopup'] .changeActionBar").click();
}
var hideWmsPopup =  function() {
    $("div[id*='wmsPopup']").css("display", "none");
}

var displayLoadingImg =  function() {
    document.getElementById("loadbg").style.display = "block";
}

var hideLoadingImg =  function() {
    document.getElementById("loadbg").style.display = "none";
}

var loadLayout = function() {

    displayLoadingImg();

    // création du layout "interne" contenant les parties tabs (bouttons + panels)
    var oTabsContainerLayout = oTabs.layout(
    {
        name                 : 'tabsContainerLayout'
        , 
        center__paneSelector : "#viewerTab"
        , 
        resizable            : false
        , 
        slidable             : false
        , 
        closable             : false
        , 
        center__onresize     : resizeTabPanelLayout // resize VISIBLE tabPanelLayouton
    });
	
    setDynamicHeightToPortletBody();

    // Resize layouts
    resizeTabPanelLayout();
    resizeSidebarLayout();

    // gestion des boutons
    addButtonsEvents( $('div#mainContainer') );


    $(window).resize(
        function() {
            setDynamicHeightToPortletBody();
            resizeTabPanelLayout();
        //MapFaces.findMapByCompId("mainMap");
        //handle_resizeResultMetadataModal();
        }
        );

    // pour faire bouger les setter en màªme temps que le scroll...
    $('div#viewerTab > div.ui-layout-west > div.ui-layout-content > div.inner-ui-layout-center > div.ui-layout-content').scroll(
        function() {
            setCoucheSetterPosition();
            return false;
        }
        );  
  
    if (window && window.MapFaces)
        window.MapFaces.reloadAllMaps(true);

    /*if (window && window.MapFaces && window.MapFaces.maps)
        for (var i in window.MapFaces.maps) {
            var map = window.MapFaces.maps[i];
            if (map.baseLayer && (map.baseLayer.resolutions[0] == map.baseLayer.resolutions[1])) {
                map.baseLayer.onMapResize();
            }
        }*/
    hideLoadingImg();
}
$(document).ready(loadLayout);

/**
 * This function display the loader correctly when you try to load a GetCapabilities.
 */
function adjustWmsAddLoader() {
    var loaderContainer = $(".wmsadd-loader");
    
    if (loaderContainer) {
        
        if (loaderContainer.css("display") == "none") {
            loaderContainer.css("display", "block");
            
        } else {
            loaderContainer.css("display", "none");
        }
        
        if (loaderContainer.css("top") == "0px") {
            var top = 52;
            if ($(".wmslist").length == 1)
                top = 88;

            loaderContainer.css("top", top + "px");
        }
    }    
}


/**
 * This function display a popup when a html component has is name attribute sets to 'modal' 
 * and a href attribute sets to a metaDataUrl
 */
$(document).ready(function() { 
    //select all the a tags with name equal to modal
    $('a[name=modal]').live('click', function(e) {
        // store the link that was clicked
        var layerName = $(this).text();
        // cancel the link behavior
        e.preventDefault();
        // concat mdViewerUrl with the href value
        var url = $('#configmdviewerurl').attr('value') + $(this).attr('href');
        if (!url || url == "") {
            return;
        }
        // load CSS
        var cssNode = document.createElement('link');
        cssNode.setAttribute('rel', 'stylesheet');
        cssNode.setAttribute('type', 'text/css');
        cssNode.setAttribute('href', 'https://www.ifremer.fr/sextant_doc/popup/css/popup.debug.css');
        document.getElementsByTagName('head')[0].appendChild(cssNode);
        // create the popup div if required
        if (!$('#popup-dialog-div').length) {
            $('body').append($('<div style="display:none;overflow:hidden;" id="popup-dialog-div" />'));
        }
        
        // open popup
        $('#popup-dialog-div').dialog({
            autoOpen: true,
            modal: true,
            resizable: false,
            height: Math.floor(parseInt($(window).height(),10) * 0.75),
            width: Math.floor(parseInt($(window).width(),10) * 0.75),
            draggable: false,
            closeText: 'Fermer',
            title: $(this).attr('value') || layerName,
            open: function() {
                $(this).html('<iframe frameborder="0" width="100%" height="100%" src="'+url+'"></iframe>');
            }
        });
        
        
    });
});

function updateXmlContainer(boutton, containerId, popupContainer) {
    var container = document.getElementById(containerId);
    var popup = popupContainer.childNodes[1];
    
    if(container.style.display == "none") {
        boutton.className = boutton.className.replace('disabled','enabled');
        container.style.display = "block";  
        jQuery(popup).animate({
            width:"1200px",
            marginLeft:"-600px"
        }, 800);
            
    } else {
        boutton.className = boutton.className.replace('enabled','disabled');
        container.style.display = "none";
        jQuery(popup).animate({
            width:"424px",
            marginLeft:"-212px"
        }, 800);
    }
}