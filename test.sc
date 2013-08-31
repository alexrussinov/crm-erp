import java.text.Normalizer
import lib.CustomIO
import java.io.File
import models.FileImp
import play.api.libs.json._



















































































































































































































































































































































val product = <prestashop xmlns:xlink="http://www.w3.org/1999/xlink">
  <product>
    <id>1</id>
    <id_manufacturer xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/manufacturers/1">1</id_manufacturer>
    <id_supplier xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/suppliers/1">1</id_supplier>
    <id_category_default xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/categories/3">3</id_category_default>
    <new/>
    <cache_default_attribute>0</cache_default_attribute>
    <id_default_image not_filterable="true" xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/images/products/1/15">15</id_default_image>
    <id_default_combination not_filterable="true" xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/18">18</id_default_combination>
    <id_tax_rules_group xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/tax_rules_group/1">1</id_tax_rules_group>
    <position_in_category not_filterable="true">0</position_in_category>
    <manufacturer_name not_filterable="true">Apple Computer, Inc</manufacturer_name>
    <quantity not_filterable="true">0</quantity>
    <type not_filterable="true">simple</type>
    <id_shop_default>1</id_shop_default>
    <reference>demo_1</reference>
    <supplier_reference/>
    <location/>
    <width>0.000000</width>
    <height>0.000000</height>
    <depth>0.000000</depth>
    <weight>0.500000</weight>
    <quantity_discount>0</quantity_discount>
    <ean13>0</ean13>
    <upc/>
    <cache_is_pack>0</cache_is_pack>
    <cache_has_attachments>0</cache_has_attachments>
    <is_virtual>0</is_virtual>
    <on_sale>0</on_sale>
    <online_only>0</online_only>
    <ecotax>0.000000</ecotax>
    <minimal_quantity>1</minimal_quantity>
    <price>199</price>
    <wholesale_price>70.000000</wholesale_price>
    <unity/>
    <unit_price_ratio>0.000000</unit_price_ratio>
    <additional_shipping_cost>0.00</additional_shipping_cost>
    <customizable>0</customizable>
    <text_fields>0</text_fields>
    <uploadable_files>0</uploadable_files>
    <active>1</active>
    <redirect_type/>
    <id_product_redirected>0</id_product_redirected>
    <available_for_order>1</available_for_order>
    <available_date>0000-00-00</available_date>
    <condition>new</condition>
    <show_price>1</show_price>
    <indexed>1</indexed>
    <visibility>both</visibility>
    <advanced_stock_management>0</advanced_stock_management>
    <date_add>2013-05-16 12:12:39</date_add>
    <date_upd>2013-05-16 12:12:39</date_upd>
    <meta_description>
      <language xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/languages/1" id="1"/>
    </meta_description>
    <meta_keywords>
      <language xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/languages/1" id="1"/>
    </meta_keywords>
    <meta_title>
      <language xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/languages/1" id="1"/>
    </meta_title>
    <link_rewrite>
      <language xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/languages/1" id="1">ipod-nano</language>
    </link_rewrite>
    <name>
      <language xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/languages/1" id="1">iPod Nano</language>
    </name>
    <description>
      <language xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/languages/1" id="1">
        <p><span style="font-size: small;"><strong>Des courbes avantageuses.</strong></span></p> <p>Pour les amateurs de sensations, voici neuf nouveaux coloris. Et ce n'est pas tout ! Faites l'expérience du design elliptique en aluminum et verre. Vous ne voudrez plus le lâcher.</p> <p><strong><span style="font-size: small;">Beau et intelligent.</span></strong></p> <p>La nouvelle fonctionnalité Genius fait d'iPod nano votre DJ personnel. Genius crée des listes de lecture en recherchant dans votre bibliothèque les chansons qui vont bien ensemble.</p> <p><strong><span style="font-size: small;">Fait pour bouger avec vous.</span></strong></p> <p>iPod nano est équipé de l'accéléromètre. Secouez-le pour mélanger votre musique. Basculez-le pour afficher Cover Flow. Et découvrez des jeux adaptés à vos mouvements.</p>
      </language>
    </description>
    <description_short>
      <language xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/languages/1" id="1">
        <p>Nouveau design. Nouvelles fonctionnalités. Désormais en 8 et 16 Go. iPod nano, plus rock que jamais.</p>
      </language>
    </description_short>
    <available_now>
      <language xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/languages/1" id="1">En stock</language>
    </available_now>
    <available_later>
      <language xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/languages/1" id="1"/>
    </available_later>
    <associations>
      <categories node_type="category">
        <category xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/categories/2">
          <id>2</id>
        </category>
        <category xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/categories/3">
          <id>3</id>
        </category>
      </categories>
      <images node_type="image">
        <image xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/images/products/1/15">
          <id>15</id>
        </image>
        <image xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/images/products/1/16">
          <id>16</id>
        </image>
        <image xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/images/products/1/17">
          <id>17</id>
        </image>
        <image xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/images/products/1/18">
          <id>18</id>
        </image>
        <image xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/images/products/1/19">
          <id>19</id>
        </image>
        <image xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/images/products/1/20">
          <id>20</id>
        </image>
        <image xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/images/products/1/21">
          <id>21</id>
        </image>
        <image xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/images/products/1/22">
          <id>22</id>
        </image>
      </images>
      <combinations node_type="combinations">
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/12">
          <id>12</id>
        </combinations>
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/13">
          <id>13</id>
        </combinations>
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/14">
          <id>14</id>
        </combinations>
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/15">
          <id>15</id>
        </combinations>
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/16">
          <id>16</id>
        </combinations>
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/17">
          <id>17</id>
        </combinations>
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/18">
          <id>18</id>
        </combinations>
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/19">
          <id>19</id>
        </combinations>
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/20">
          <id>20</id>
        </combinations>
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/21">
          <id>21</id>
        </combinations>
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/22">
          <id>22</id>
        </combinations>
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/23">
          <id>23</id>
        </combinations>
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/24">
          <id>24</id>
        </combinations>
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/25">
          <id>25</id>
        </combinations>
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/26">
          <id>26</id>
        </combinations>
        <combinations xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/combinations/27">
          <id>27</id>
        </combinations>
      </combinations>
      <product_option_values node_type="product_options_values">
        <product_options_values xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/product_option_values/4">
          <id>4</id>
        </product_options_values>
        <product_options_values xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/product_option_values/16">
          <id>16</id>
        </product_options_values>
        <product_options_values xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/product_option_values/15">
          <id>15</id>
        </product_options_values>
        <product_options_values xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/product_option_values/19">
          <id>19</id>
        </product_options_values>
        <product_options_values xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/product_option_values/3">
          <id>3</id>
        </product_options_values>
        <product_options_values xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/product_option_values/14">
          <id>14</id>
        </product_options_values>
        <product_options_values xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/product_option_values/7">
          <id>7</id>
        </product_options_values>
        <product_options_values xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/product_option_values/5">
          <id>5</id>
        </product_options_values>
        <product_options_values xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/product_option_values/6">
          <id>6</id>
        </product_options_values>
        <product_options_values xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/product_option_values/18">
          <id>18</id>
        </product_options_values>
      </product_option_values>
      <product_features node_type="product_feature"/>
      <tags node_type="tag">
        <tag xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/tags/5">
          <id>5</id>
        </tag>
        <tag xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/tags/11">
          <id>11</id>
        </tag>
        <tag xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/tags/12">
          <id>12</id>
        </tag>
      </tags>
      <stock_availables node_type="stock_available">
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/1">
          <id>1</id>
          <id_product_attribute>0</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/19">
          <id>19</id>
          <id_product_attribute>12</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/20">
          <id>20</id>
          <id_product_attribute>13</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/21">
          <id>21</id>
          <id_product_attribute>14</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/22">
          <id>22</id>
          <id_product_attribute>15</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/23">
          <id>23</id>
          <id_product_attribute>16</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/24">
          <id>24</id>
          <id_product_attribute>17</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/25">
          <id>25</id>
          <id_product_attribute>18</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/26">
          <id>26</id>
          <id_product_attribute>19</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/27">
          <id>27</id>
          <id_product_attribute>20</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/28">
          <id>28</id>
          <id_product_attribute>21</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/29">
          <id>29</id>
          <id_product_attribute>22</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/30">
          <id>30</id>
          <id_product_attribute>23</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/31">
          <id>31</id>
          <id_product_attribute>24</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/32">
          <id>32</id>
          <id_product_attribute>25</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/33">
          <id>33</id>
          <id_product_attribute>26</id_product_attribute>
        </stock_available>
        <stock_available xlink:href="http://localhost:8080/prestashop_1.5.4.1/prestashop/api/stock_availables/34">
          <id>34</id>
          <id_product_attribute>27</id_product_attribute>
        </stock_available>
      </stock_availables>
      <accessories node_type="product"/>
      <product_bundle node_type="products"/>
    </associations>
  </product>
</prestashop>







































































val pr = ProductPresta.fromXml(product \ "product")













val image = (product \ "product" \ "id_default_image" )(0)











val uri : String = image.attribute(image.getNamespace("xlink"),"href").getOrElse(None).toString







uri.split("//")

























































































































































































































































































































































































































































