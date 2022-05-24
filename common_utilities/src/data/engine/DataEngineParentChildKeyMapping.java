package data.engine;

import java.util.HashMap;
import java.util.Map;

public class DataEngineParentChildKeyMapping {
	public static Map<String,String> mapping = new HashMap<String, String>();
	
	static{
		mapping.put("sp_greater_than_500_product_name","sp_greater_than_500");
		mapping.put("sp_greater_than_500_pdp_url","sp_greater_than_500");
		mapping.put("sp_greater_than_500_plp_url","sp_greater_than_500");
		
		mapping.put("app_sp_greater_than_500_product_name", "app_only_sp_greater_than_500");
		mapping.put("app_sp_greater_than_500_pdp_url", "app_only_sp_greater_than_500");
		mapping.put("app_sp_greater_than_500_plp_url", "app_only_sp_greater_than_500");
		
		
		mapping.put("sp_backorder_quantity_product_name", "backorder_quantity_product");
		mapping.put("sp_backorder_quantity_pdp_url", "backorder_quantity_product");
		mapping.put("backorder_quantity", "backorder_quantity_product");
		mapping.put("backorder_status", "backorder_quantity_product");

		mapping.put("cp_greater_than_500_product_name","cp_greater_than_500");
		mapping.put("cp_greater_than_500_child_product_name","cp_greater_than_500");
		mapping.put("cp_greater_than_500_pdp_url","cp_greater_than_500");
		mapping.put("cp_greater_than_500_plp_url","cp_greater_than_500");

		mapping.put("size_footwear_product_greater_than_500_product_name","sizeable_footwear_more_than_500");
		mapping.put("size_footwear_product_greater_than_500_child_product_name","sizeable_footwear_more_than_500");
		mapping.put("size_footwear_product_greater_than_500_pdp_url","sizeable_footwear_more_than_500");
		mapping.put("size_footwear_product_greater_than_500_plp_url","sizeable_footwear_more_than_500");
		
		
		mapping.put("sp_oos_greater_than_500_product_name","sp_oos_greater_than_500");
		mapping.put("sp_oos_greater_than_500_pdp_url","sp_oos_greater_than_500");
		mapping.put("sp_oos_greater_than_500_plp_url","sp_oos_greater_than_500");

		mapping.put("cp_size_chart_greater_than_500_product_name","cp_size_chart_greater_than_500");
		mapping.put("cp_size_chart_greater_than_500_child_product_name","cp_size_chart_greater_than_500");
		mapping.put("cp_size_chart_greater_than_500_pdp_url","cp_size_chart_greater_than_500");
		mapping.put("cp_size_chart_greater_than_500_plp_url","cp_size_chart_greater_than_500");

		mapping.put("cp_size_chart_oos_greater_than_500_product_name","cp_size_chart_oos_greater_than_500");
		mapping.put("cp_size_chart_oos_greater_than_500_child_product_name","cp_size_chart_oos_greater_than_500");
		mapping.put("cp_size_chart_oos_greater_than_500_pdp_url","cp_size_chart_oos_greater_than_500");
		mapping.put("cp_size_chart_oos_greater_than_500_plp_url","cp_size_chart_oos_greater_than_500");
	
		mapping.put("cp_oos_greater_than_500_product_name","cp_oos_greater_than_500");
		mapping.put("cp_oos_greater_than_500_child_product_name","cp_oos_greater_than_500");
		mapping.put("cp_oos_greater_than_500_pdp_url","cp_oos_greater_than_500");
		mapping.put("cp_oos_greater_than_500_plp_url","cp_oos_greater_than_500");
		
		mapping.put("cp_less_than_500_product_name","cp_less_than_500");
		mapping.put("cp_less_than_500_pdp_url","cp_less_than_500");
		mapping.put("cp_less_than_500_plp_url","cp_less_than_500");
		mapping.put("cp_less_than_500_product_id","cp_less_than_500");
		mapping.put("cp_less_than_500_child_id","cp_less_than_500");
		mapping.put("cp_less_than_500_child_product_name","cp_less_than_500");

		mapping.put("sp_less_than_500_product_name","sp_less_than_500");
		mapping.put("sp_less_than_500_pdp_url","sp_less_than_500");
		mapping.put("sp_less_than_500_plp_url","sp_less_than_500");
		mapping.put("sp_less_than_500_product_price","sp_less_than_500");
		mapping.put("sp_less_than_500_plp_product_count","sp_less_than_500");

		mapping.put("mp_product_product_name","mp_product");
		mapping.put("mp_product_pdp_url","mp_product");
		mapping.put("mp_product_plp_url","mp_product");
		
		mapping.put("bundle_product_product_name","bundle_product");
		mapping.put("bundle_product_pdp_url","bundle_product");
		mapping.put("bundle_product_plp_url","bundle_product");

		mapping.put("offer_product_name","offer_product");
		mapping.put("offer_product_pdp_url","offer_product");
		mapping.put("offer_product_plp","offer_product");
		mapping.put("offer_message","offer_product");
		mapping.put("offer_product_add_to_bag","offer_product");
		mapping.put("dynamic_text","offer_product");

		mapping.put("offer_pro_product_name","pro_offer_product");
		mapping.put("offer_pro_product_pdp_url","pro_offer_product");
		mapping.put("offer_pro_product_plp","pro_offer_product");
		mapping.put("offer_pro_message","pro_offer_product");
		mapping.put("offer_pro_product_add_to_bag","pro_offer_product");
		mapping.put("offer_pro_dynamic_text","pro_offer_product");

		mapping.put("non_pro_product_name","non_pro_product");
		mapping.put("non_pro_product_pdp_url","non_pro_product");
		mapping.put("non_pro_product_plp_url","non_pro_product");
		mapping.put("non_pro_product_price","non_pro_product");

		mapping.put("10_product_name","N_product_10");
		mapping.put("10_product_pdp_url","N_product_10");
		mapping.put("10_product_plp_url","N_product_10");
		mapping.put("10_product_price","N_product_10");
		mapping.put("10_product_quantity","N_product_10");
		
		mapping.put("100_product_name","N_product_100");
		mapping.put("100_product_pdp_url","N_product_100");
		mapping.put("100_product_plp_url","N_product_100");
		mapping.put("100_product_price","N_product_100");
		mapping.put("100_product_quantity","N_product_100");

		mapping.put("both_nykaa_pro_product_name","both_pro_nykaa_product");
		mapping.put("both_nykaa_pro_product_pdp_url","both_pro_nykaa_product");
		mapping.put("both_nykaa_pro_product_plp","both_pro_nykaa_product");
		mapping.put("both_nykaa_pro_product_price","both_pro_nykaa_product");

		mapping.put("lux_brand_product_name","luxe_brand_product");
		mapping.put("lux_brand_product_url","luxe_brand_product");
		mapping.put("lux_brand_plp_url","luxe_brand_product");
		mapping.put("lux_brand_product_price","luxe_brand_product");

		mapping.put("review_product_name","review_product");
		mapping.put("review_product_url","review_product");
		mapping.put("review_plp_url","review_product");
		mapping.put("review_product_price","review_product");
		mapping.put("review_product_review_count","review_product");
		mapping.put("review_product_rating_count","review_product");
		mapping.put("verified_buyer","review_product");
		mapping.put("image_count","review_product");
		mapping.put("verified_buyer_count","review_product");

		
		mapping.put("beauty_discount_product_name","beauty_discount_product");
		mapping.put("beauty_discount_product_url","beauty_discount_product");
		mapping.put("beauty_discount_plp_url","beauty_discount_product");
		mapping.put("beauty_discount_product_price","beauty_discount_product");
		mapping.put("beauty_product_discount","beauty_discount_product");

		mapping.put("ngs_greater_than_500_product_name","ngs_greater_than_500");
		mapping.put("ngs_greater_than_500_pdp_url","ngs_greater_than_500");
		mapping.put("ngs_product_id","ngs_greater_than_500");

		mapping.put("scp_greater_than_500_product_name","sizeable_more_than_500");
		mapping.put("scp_greater_than_500_child_product_name","sizeable_more_than_500");
		mapping.put("scp_greater_than_500_pdp_url","sizeable_more_than_500");
		mapping.put("scp_greater_than_500_plp_url","sizeable_more_than_500");

		mapping.put("zero_rating_product_name","product_rating_count_zero");
		mapping.put("zero_rating_product_url","product_rating_count_zero");
		mapping.put("zero_rating_plp_url","product_rating_count_zero");
		mapping.put("rating_count_zero","product_rating_count_zero");

		mapping.put("product_name_explore_more","sp_less_than_500");
		mapping.put("product_url_explore_more","product_rating_count_zero");
		
		mapping.put("wishlist_product_name","wishlist_product");
		mapping.put("wishlist_product_url","wishlist_product");
		mapping.put("wishlist_plp_url","wishlist_product");
		mapping.put("wishlist_product_price","wishlist_product");

		mapping.put("product_name_combo","sp_less_than_500");
		mapping.put("product_url_combo","sp_less_than_500");
		
		mapping.put("view_products_offer_product_name","view_products_offer");
		mapping.put("view_products_offer_product_pdp_url","view_products_offer");
		mapping.put("view_products_offer_product_plp","view_products_offer");
		mapping.put("view_products_offer_message","view_products_offer");
		mapping.put("view_products_offer_product_add_to_bag","view_products_offer");
		mapping.put("view_products_dynamic_text","view_products_offer");

		mapping.put("sp_greater_than_500_NEW_tag_product_name","sp_greater_than_500_NEW_tag");
		mapping.put("sp_greater_than_500_NEW_tag_pdp_url","sp_greater_than_500_NEW_tag");
		mapping.put("sp_greater_than_500_NEW_tag_plp_url","sp_greater_than_500_NEW_tag");

		mapping.put("cp_shoe_size_variant_greater_than_500_product_name","cp_shoe_size_variant_greater_than_500");
		mapping.put("cp_shoe_size_variant_greater_than_500_pdp_url","cp_shoe_size_variant_greater_than_500");
		mapping.put("cp_shoe_size_variant_greater_than_500_plp_url","cp_shoe_size_variant_greater_than_500");

		mapping.put("cp_hair_color_shade_variant_greater_than_500_product_name","cp_hair_color_shade_variant");
		mapping.put("cp_hair_color_shade_variant_greater_than_500_pdp_url","cp_hair_color_shade_variant");
		mapping.put("cp_hair_color_shade_variant_greater_than_500_plp_url","cp_hair_color_shade_variant");

		mapping.put("cp_cloth_size_chart_greater_than_500_product_name","cp_cloth_size_chart_greater_than_500");
		mapping.put("cp_cloth_size_chart_greater_than_500_pdp_url","cp_cloth_size_chart_greater_than_500");
		mapping.put("cp_cloth_size_chart_greater_than_500_plp_url","cp_cloth_size_chart_greater_than_500");
		
	}
}
