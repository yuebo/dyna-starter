package com.ifreelight.dyna.provider.option;

import com.ifreelight.dyna.core.ViewContext;
import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yuebo on 2017/12/6.
 */
@Component
public class IconOptionProvider extends DefaultOptionProvider {
    @Override
    protected List<Map<String, Object>> findOptions(ViewContext viewContext, String table, Map query, ListOrderedMap sort, int i, int i1) {
        String icons=
                "glyphicon-glass\n" +
                "glyphicon-music\n" +
                "glyphicon-search\n" +
                "glyphicon-envelope\n" +
                "glyphicon-heart\n" +
                "glyphicon-star\n" +
                "glyphicon-star-empty\n" +
                "glyphicon-user\n" +
                "glyphicon-film\n" +
                "glyphicon-th-large\n" +
                "glyphicon-th\n" +
                "glyphicon-th-list\n" +
                "glyphicon-ok\n" +
                "glyphicon-remove\n" +
                "glyphicon-zoom-in\n" +
                "glyphicon-zoom-out\n" +
                "glyphicon-off\n" +
                "glyphicon-signal\n" +
                "glyphicon-cog\n" +
                "glyphicon-trash\n" +
                "glyphicon-home\n" +
                "glyphicon-file\n" +
                "glyphicon-time\n" +
                "glyphicon-road\n" +
                "glyphicon-download-alt\n" +
                "glyphicon-download\n" +
                "glyphicon-upload\n" +
                "glyphicon-inbox\n" +
                "glyphicon-play-circle\n" +
                "glyphicon-repeat\n" +
                "glyphicon-refresh\n" +
                "glyphicon-list-alt\n" +
                "glyphicon-lock\n" +
                "glyphicon-flag\n" +
                "glyphicon-headphones\n" +
                "glyphicon-volume-off\n" +
                "glyphicon-volume-down\n" +
                "glyphicon-volume-up\n" +
                "glyphicon-qrcode\n" +
                "glyphicon-barcode\n" +
                "glyphicon-tag\n" +
                "glyphicon-tags\n" +
                "glyphicon-book\n" +
                "glyphicon-bookmark\n" +
                "glyphicon-print\n" +
                "glyphicon-camera\n" +
                "glyphicon-font\n" +
                "glyphicon-bold\n" +
                "glyphicon-italic\n" +
                "glyphicon-text-height\n" +
                "glyphicon-text-width\n" +
                "glyphicon-align-left\n" +
                "glyphicon-align-center\n" +
                "glyphicon-align-right\n" +
                "glyphicon-align-justify\n" +
                "glyphicon-list\n" +
                "glyphicon-indent-left\n" +
                "glyphicon-indent-right\n" +
                "glyphicon-facetime-video\n" +
                "glyphicon-picture\n" +
                "glyphicon-pencil\n" +
                "glyphicon-map-marker\n" +
                "glyphicon-adjust\n" +
                "glyphicon-tint\n" +
                "glyphicon-edit\n" +
                "glyphicon-share\n" +
                "glyphicon-check\n" +
                "glyphicon-move\n" +
                "glyphicon-step-backward\n" +
                "glyphicon-fast-backward\n" +
                "glyphicon-backward\n" +
                "glyphicon-play\n" +
                "glyphicon-pause\n" +
                "glyphicon-stop\n" +
                "glyphicon-forward\n" +
                "glyphicon-fast-forward\n" +
                "glyphicon-step-forward\n" +
                "glyphicon-eject\n" +
                "glyphicon-chevron-left\n" +
                "glyphicon-chevron-right\n" +
                "glyphicon-plus-sign\n" +
                "glyphicon-minus-sign\n" +
                "glyphicon-remove-sign\n" +
                "glyphicon-ok-sign\n" +
                "glyphicon-question-sign\n" +
                "glyphicon-info-sign\n" +
                "glyphicon-screenshot\n" +
                "glyphicon-remove-circle\n" +
                "glyphicon-ok-circle\n" +
                "glyphicon-ban-circle\n" +
                "glyphicon-arrow-left\n" +
                "glyphicon-arrow-right\n" +
                "glyphicon-arrow-up\n" +
                "glyphicon-arrow-down\n" +
                "glyphicon-share-alt\n" +
                "glyphicon-resize-full\n" +
                "glyphicon-resize-small\n" +
                "glyphicon-plus\n" +
                "glyphicon-minus\n" +
                "glyphicon-asterisk\n" +
                "glyphicon-exclamation-sign\n" +
                "glyphicon-gift\n" +
                "glyphicon-leaf\n" +
                "glyphicon-fire\n" +
                "glyphicon-eye-open\n" +
                "glyphicon-eye-close\n" +
                "glyphicon-warning-sign\n" +
                "glyphicon-plane\n" +
                "glyphicon-calendar\n" +
                "glyphicon-random\n" +
                "glyphicon-comment\n" +
                "glyphicon-magnet\n" +
                "glyphicon-chevron-up\n" +
                "glyphicon-chevron-down\n" +
                "glyphicon-retweet\n" +
                "glyphicon-shopping-cart\n" +
                "glyphicon-folder-close\n" +
                "glyphicon-folder-open\n" +
                "glyphicon-resize-vertical\n" +
                "glyphicon-resize-horizontal\n" +
                "glyphicon-hdd\n" +
                "glyphicon-bullhorn\n" +
                "glyphicon-bell\n" +
                "glyphicon-certificate\n" +
                "glyphicon-thumbs-up\n" +
                "glyphicon-thumbs-down\n" +
                "glyphicon-hand-right\n" +
                "glyphicon-hand-left\n" +
                "glyphicon-hand-up\n" +
                "glyphicon-hand-down\n" +
                "glyphicon-circle-arrow-right\n" +
                "glyphicon-circle-arrow-left\n" +
                "glyphicon-circle-arrow-up\n" +
                "glyphicon-circle-arrow-down\n" +
                "glyphicon-globe\n" +
                "glyphicon-wrench\n" +
                "glyphicon-tasks\n" +
                "glyphicon-filter\n" +
                "glyphicon-briefcase\n" +
                "glyphicon-fullscreen";
        List<Map<String, Object>> result=new ArrayList();
        for(String icon:icons.split("\n")){
            Map map=new LinkedCaseInsensitiveMap();
            map.put("icon",icon);
            map.put("label","<i class=\"glyphicon "+icon+"\"></i>"+icon);
            result.add(map);
        }
        return result;
    }
}
