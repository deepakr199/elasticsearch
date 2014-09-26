var shopApp = angular.module('shopApp', ['ngResource','ngRoute']);
 shopApp.controller(
			'itemController',
			function($scope, $http, $location) {
				
				$scope.myFunction = function(searchText) {
					$('#categoryblock').show();
					$('#subcategoryblock').show();
					$('#productController').show();
					  
					  $('ol.breadcrumb').html("<li><a href=\"\" onclick=\"breadcrumbcall('','')\" >Home</a></li>");


					$('#filter').val("");
				    $('#id').val("");

					$http.get("/shop/items?searchText="+searchText).
             	 	success(function(data, status, headers, config) {
             	 		$scope.items = data.items;
             	 		$scope.totalPages = data.totalPages;
             	 		
             	 		 $('#page-selection').bootpag({
          		            total: Math.ceil($scope.totalPages/12)});
	             }).error(function(data, status, headers, config) {
	             });
					
					angular.element('#featuredCtrl').scope().getFeatured(searchText,"","");
					angular.element('#brandController').scope().getBrands(searchText,"","");
					angular.element('#categoryController').scope().getCategories(searchText,"","");
					angular.element('#productController').scope().getProductTypes(searchText,"","");

				};
				
				$scope.filter = function(filter) {
					var queries = filter.split(":");
					var searchText = $("#searchText").val();
					var filter = $('#filter').val();
					var id = $('#id').val();
					$http.get("/shop/items?searchText="+searchText+"&sortBy="+queries[0]+"&sortOrder="+queries[1]+"&filter="+filter+"&id="+id).
             	 	success(function(data, status, headers, config) {
             	 		$scope.items = data.items;
             	 		$scope.totalPages = data.totalPages;
            	 		 $('#page-selection').bootpag({
           		            total: Math.ceil($scope.totalPages/12)
           		           });
	             }).error(function(data, status, headers, config) {
	             });
					angular.element('#featuredCtrl').scope().getFeatured(searchText,filter,id);
					angular.element('#brandController').scope().getBrands(searchText,filter,id);
					angular.element('#categoryController').scope().getCategories(searchText,filter,id);
					angular.element('#productController').scope().getProductTypes(searchText,filter,id);

				};
				
				
				$scope.itemFilter = function(searchText, filter, id) {
					var queries = $('.selectpicker').val().split(":");
					$http.get("/shop/items?searchText="+searchText+"&filter="+filter+"&id="+id+"&sortBy="+queries[0]+"&sortOrder="+queries[1]).
             	 	success(function(data, status, headers, config) {
             	 		$scope.items = data.items;
             	 		$scope.totalPages = data.totalPages;
            	 		 $('#page-selection').bootpag({
           		            total: Math.ceil($scope.totalPages/12)});
	             }).error(function(data, status, headers, config) {
	             });
					angular.element('#featuredCtrl').scope().getFeatured(searchText,filter,id);
					angular.element('#brandController').scope().getBrands(searchText,filter,id);
					angular.element('#categoryController').scope().getCategories(searchText,filter,id);
					angular.element('#productController').scope().getProductTypes(searchText,filter,id);

				};
				
				$scope.itemPaginate = function(searchText, filter, id, sortBy, sortOrder, from){
					from = (from-1)*12;
					searchText = $('input#searchText').val();
					var queries = $('.selectpicker').val().split(":");
					sortBy= queries[0];
					sortOrder = queries[1];
					var filter = $('#filter').val();
					var id = $('#id').val();

					$http.get("/shop/items?searchText="+searchText+"&filter="+filter+"&id="+id+"&sortBy="+sortBy+"&sortOrder="+sortOrder+"&page="+from).
             	 	success(function(data, status, headers, config) {
             	 		$scope.items = data.items;
             	 		$scope.totalPages = data.totalPages;
//            	 		 $('#page-selection').bootpag({
//           		            total: Math.ceil($scope.totalPages/12),
//           		            maxVisible: 10,
//           		            page:1
//           		        }).on("page", function(event, /* page number here */ num){
//        					var queries = $('.selectpicker').val().split(":");
//           		        	console.log(filter+"--"+id+"--"+$('#searchText').val()+"--"+searchText);
//           		        	 console.log(num);
//           		        });
	             }).error(function(data, status, headers, config) {
	             });
					
				};
				
				 $http.get("/shop/items").
             	 	success(function(data, status, headers, config) {
             	 		$scope.items = data.items;
             	 		$scope.totalPages = data.totalPages;
             	 		
             	 	    $('#page-selection').bootpag({
         		            total: Math.ceil($scope.totalPages/12),
         		            maxVisible: 10,
         		            page:1
         		        }).on("page", function(event, /* page number here */ num){
             				 angular.element('#itemController').scope().itemPaginate("","","","pricing.on_sale","desc",num);
         		        });
             	 		
	             }).error(function(data, status, headers, config) {
	             });
				 
					angular.element('#featuredCtrl').scope().getFeatured("","","");
					angular.element('#brandController').scope().getBrands("","","");
					angular.element('#categoryController').scope().getCategories("","","");
					angular.element('#productController').scope().getProductTypes("","","");
				
			}); 

 
 shopApp.directive('myRepeatDirectiveSub', function() {
	 return function(scope, element, attrs) {
	 if (scope.$last) setTimeout(function(){
	 scope.$emit('onRepeatLastSub', element, attrs);
	 }, 1);
	 };
	 })
	 
	  shopApp.directive('myRepeatDirectiveProduct', function() {
	 return function(scope, element, attrs) {
	 if (scope.$last) setTimeout(function(){
	 scope.$emit('onRepeatLastProduct', element, attrs);
	 }, 1);
	 };
	 })
	 
	 	  shopApp.directive('myRepeatDirectiveBrand', function() {
	 return function(scope, element, attrs) {
	 if (scope.$last) setTimeout(function(){
	 scope.$emit('onRepeatLastBrand', element, attrs);
	 }, 1);
	 };
	 })
 
 shopApp.controller(
			'categoryController',
			function($scope, $http, $location) {
				$scope.getCategories = function(searchText,filter,id) {	

				$http.get("/shop/categories?searchText="+searchText+"&filter="+filter+"&id="+id).
       	 	success(function(data, status, headers, config) {
       	 		$scope.categories = data.mainObject[0];
       	 		$scope.subcategories = data.mainObject[1];
       	 		$scope.totalCategories = data.totalCategories;
       	 		$scope.totalSubCategories = data.totalSubCategories;
       	 		
       	 	 $scope.$on('onRepeatLastSub', function(scope, element, attrs){
       	 		 if($scope.subcategories.length <=10){
       	 			 $('li.subcategorymore').remove();
       	 		 }else{
       	 			 $('ul.subcategory').append("<li class=\"subcategoryless\" ><a href=\"\" class=\"blue\"><span class=\"pull-right\">Less..</span></a></li>")
           	 		 $('li.subcategoryless').trigger("click");
       	 			 $('li.subcategorymore').show();
       	 		 }

       	 	 });

	             }).error(function(data, status, headers, config) {
	             });
				
				};
				
				$scope.getByCategory = function(id, filter, category) {
					var html = "<li>" + $('ol.breadcrumb li').slice(0).html() + "</li>";
					var breadcrumb =  "<li><a href=\"javascript:breadcrumbcall('"+id+"','"+filter+"')\">"+category+"</a></li>";
					$('ol.breadcrumb').html(html+breadcrumb);
					
					var searchText = $('#searchText').val();
					$('#filter').val(filter);
					$('#id').val(id);
					$('#categoryblock').hide();
					angular.element('#itemController').scope().itemFilter(searchText, filter, id);
					
				};
					
				$scope.getBySubCategory = function(id, filter, subCategory, category, categoryid) {	
					var categoryhtml = $('ol.breadcrumb li').slice(1).html();
					if (typeof categoryhtml === "undefined") {
						categoryhtml = "<a href=\"javascript:breadcrumbcall('"+categoryid+"','categories')\">"+category+"</a>";
					}
					var html = "<li>" + $('ol.breadcrumb li').slice(0).html() + "</li>"+"<li>" + categoryhtml + "</li>";
					var breadcrumb =  "<li><a href=\"javascript:breadcrumbcall('"+id+"','subcategories')\">"+subCategory+"</a></li>";
					$('ol.breadcrumb').html(html+breadcrumb);
					
					var searchText = $('#searchText').val();
					$('#filter').val(filter);
					$('#id').val(id);
					$('#categoryblock').hide();
					$('#subcategoryblock').hide();
					angular.element('#itemController').scope().itemFilter(searchText, filter, id);

				};
				
			}); 
 
 shopApp.controller(
			'featuredCtrl',
			function($scope, $http, $location) {
				$scope.getFeatured = function(searchText, filter, id) {	

				 $http.get("/shop/newOnSaleImported?searchText="+searchText+"&filter="+filter+"&id="+id).
    	 	success(function(data, status, headers, config) {
    	 		$scope.featuredI = data;
	             }).error(function(data, status, headers, config) {
	             });
				};
				
			}); 
 
 shopApp.controller(
			'brandController',
			function($scope, $http, $location) {
				 
					$scope.getBrands = function(searchText,filter, id) {
						$http.get("/shop/brands?searchText="+searchText+"&filter="+filter+"&id="+id).
	             	 	success(function(data, status, headers, config) {
	             	 		$scope.brands = data.brands;
	               	 		$scope.totalBrands = data.totalBrands;
	               	 		
	                  	 	 $scope.$on('onRepeatLastBrand', function(scope, element, attrs){
	                  	 		if($scope.brands.length <=10){
	                  	 			 $('li.brandmore').remove();
	                  	 		 }else{
	                  	 			 $('ul.brand').append("<li class=\"brandless\" ><a href=\"\" class=\"blue\"><span class=\"pull-right\">Less..</span></a></li>")
	                      	 		 $('li.brandless').trigger("click");
	                  	 			 $('li.brandmore').show();
	                  	 		 }	      	                   	 	 });

		             }).error(function(data, status, headers, config) {
		             });
					};
					
					$scope.getByBrand = function(id,filter) {	
						var searchText = $('#searchText').val();
						$('#filter').val(filter);
						$('#id').val(id);
						angular.element('#itemController').scope().itemFilter(searchText, filter, id);

					};
				
			}); 
 
 shopApp.controller(
			'productController',
			function($scope, $http, $location) {
				 
					$scope.getProductTypes = function(searchText, filter, id) {
						$http.get("/shop/producttypes?searchText="+searchText+"&filter="+filter+"&id="+id).
	             	 	success(function(data, status, headers, config) {
	             	 		$scope.types = data.types;
	               	 		$scope.totalTypes = data.totalTypes;
	                  	 	 $scope.$on('onRepeatLastProduct', function(scope, element, attrs){
	                  	 		if($scope.types.length <=10){
	                  	 			 $('li.productmore').remove();
	                  	 		 }else{
	                  	 			 $('ul.product').append("<li class=\"productless\" ><a href=\"\" class=\"blue\"><span class=\"pull-right\">Less..</span></a></li>")
	                      	 		 $('li.productless').trigger("click");
	                  	 			 $('li.productmore').show();
	                  	 		 }	                   	 	 
	                  	 		});
		             }).error(function(data, status, headers, config) {
		             });
					};
					$scope.getByType = function(id, filter, type) {	
						
						var categoryhtml = $('ol.breadcrumb li').slice(1).html();
						if (typeof categoryhtml === "undefined") {
							categoryhtml = "";
						}else{
							categoryhtml = "<li>" + categoryhtml + "</li>";
						}
						
						var subcategoryhtml = $('ol.breadcrumb li').slice(2).html();
						if (typeof subcategoryhtml === "undefined") {
							subcategoryhtml = "";
						}else{
							subcategoryhtml = "<li>" + subcategoryhtml + "</li>";
						}
						
						
						var html = "<li>" + $('ol.breadcrumb li').slice(0).html() + "</li>" + categoryhtml + subcategoryhtml;
						var breadcrumb =  "<li><a href=\"javascript:breadcrumbcall('"+id+"','"+filter+"')\" >"+type+"</a></li>";
						$('ol.breadcrumb').html(html+breadcrumb);
						
						var searchText = $('#searchText').val();
						$('#filter').val(filter);
						$('#id').val(id);
						$('#categoryblock').hide();
						$('#subcategoryblock').hide();
						$('#productController').hide();
						angular.element('#itemController').scope().itemFilter(searchText, filter, id);

					};
				
			}); 
 
shopApp.config(function($routeProvider,$locationProvider) {
	$locationProvider.html5Mode(true);
				$routeProvider.when('/shop', {
					templateUrl : '/assets/template/shop.html'
				}).when('/shop/items', {
					templateUrl : '/assets/template/shop.html'
				}).otherwise({
//				}).when('/dashboard', {
//					templateUrl : '/assets/template/ticket/dashboard.html',
//					controller: 'listController'
//				}).when('/ticket/add', {
//					templateUrl : '/assets/template/ticket/add.html',
//					controller: 'addController'
//				}).when('/ticket/:id', {
//					templateUrl : '/assets/template/ticket/edit.html',
//					controller : 'editController'
//				}).otherwise({
					redirectTo : '/shop'	
				});
			});

