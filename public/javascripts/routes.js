var shopApp = angular.module('shopApp', ['ngResource','ngRoute']);
 shopApp.controller(
			'itemController',
			function($scope, $http, $location) {
				
				$scope.myFunction = function(searchText) {
					$('#categoryblock').show();
					$('#subcategoryblock').show();
					$('#productController').show();
					  
					$('ol.breadcrumb').html("<li><a href=\"\" onclick=\"breadcrumbcall('','')\" >Home</a></li>");

					$('#categoryType').val("");
				    $('#id').val("");
					$('#brandType').val("");
				    $('#brandTypeId').val("");

					$http.get("/shop/items?searchText="+searchText).
             	 	success(function(data, status, headers, config) {
             	 		$scope.items = data.items;
             	 		$scope.totalPages = data.totalPages;
             	 		
             	 		$('#page-selection').bootpag({
          		            total: Math.ceil($scope.totalPages/12)});
	             }).error(function(data, status, headers, config) {
	             });
					
					angular.element('#itemController').scope().getFeatured(searchText,"","");
					angular.element('#brandController').scope().getBrands(searchText,"","");
					angular.element('#categoryController').scope().getCategories(searchText,"","");
					angular.element('#productController').scope().getProductTypes(searchText,"","");

				};
				
				$scope.sorter = function(sorter) {
					var filters = "";
					$('input[name="filter"]:checked').each(function() {
						   filters = filters + this.value + ":";
					});
					
					var queries = sorter.split(":");
					var searchText = $("#searchText").val();
					var query = "";
					if($('#id').val()){
						query = query + $('#categoryType').val() + ":" + $('#id').val() + "::"; 
					}
					if($('#brandTypeId').val()){
						query = query + $('#brandType').val() + ":" + $('#brandTypeId').val(); 
					}
					$http.get("/shop/items?searchText="+searchText+"&sortBy="+queries[0]+"&sortOrder="+queries[1]+"&categoryType="+query+"&filter="+filters).
             	 	success(function(data, status, headers, config) {
             	 		$scope.items = data.items;
             	 		$scope.totalPages = data.totalPages;
            	 		 $('#page-selection').bootpag({
           		            total: Math.ceil($scope.totalPages/12)
           		           });
	             }).error(function(data, status, headers, config) {
	             });
					angular.element('#itemController').scope().getFeatured(searchText,categoryType,id);
					angular.element('#brandController').scope().getBrands(searchText,categoryType,id);
					angular.element('#categoryController').scope().getCategories(searchText,categoryType,id);
					angular.element('#productController').scope().getProductTypes(searchText,categoryType,id);

				};
				
				
				$scope.filter = function() {
					var filters = "";
					$('input[name="filter"]:checked').each(function() {
						   filters = filters + this.value + ":";
					});
					
					var queries = $('.selectpicker').val().split(":");
					var searchText = $("#searchText").val();
					var query = "";
					if($('#id').val()){
						query = query + $('#categoryType').val() + ":" + $('#id').val() + "::"; 
					}
					if($('#brandTypeId').val()){
						query = query + $('#brandType').val() + ":" + $('#brandTypeId').val(); 
					}
					$http.get("/shop/items?searchText="+searchText+"&sortBy="+queries[0]+"&sortOrder="+queries[1]+"&categoryType="+query+"&filter="+filters).
             	 	success(function(data, status, headers, config) {
             	 		$scope.items = data.items;
             	 		$scope.totalPages = data.totalPages;
            	 		 $('#page-selection').bootpag({
           		            total: Math.ceil($scope.totalPages/12)
           		           });
	             }).error(function(data, status, headers, config) {
	             });
					angular.element('#itemController').scope().getFeatured(searchText,categoryType,id);
					angular.element('#brandController').scope().getBrands(searchText,categoryType,id);
					angular.element('#categoryController').scope().getCategories(searchText,categoryType,id);
					angular.element('#productController').scope().getProductTypes(searchText,categoryType,id);

				};
				
				
				$scope.itemFilter = function(searchText, categoryType, id) {
					var filters = "";
					$('input[name="filter"]:checked').each(function() {
						   filters = filters + this.value + ":";
					});
					var queries = $('.selectpicker').val().split(":");
					var query = "";
					if($('#id').val()){
						query = query + $('#categoryType').val() + ":" + $('#id').val() + "::"; 
					}
					if($('#brandTypeId').val()){
						query = query + $('#brandType').val() + ":" + $('#brandTypeId').val(); 
					}
					
					$http.get("/shop/items?searchText="+searchText+"&categoryType="+query+"&filter="+filters+"&sortBy="+queries[0]+"&sortOrder="+queries[1]).
             	 	success(function(data, status, headers, config) {
             	 		$scope.items = data.items;
             	 		$scope.totalPages = data.totalPages;
            	 		$('#page-selection').bootpag({
           		            total: Math.ceil($scope.totalPages/12)});
	             }).error(function(data, status, headers, config) {
	             });
					angular.element('#itemController').scope().getFeatured(searchText,categoryType,id);
					angular.element('#brandController').scope().getBrands(searchText,categoryType,id);
					angular.element('#categoryController').scope().getCategories(searchText,categoryType,id);
					angular.element('#productController').scope().getProductTypes(searchText,categoryType,id);

				};
				
				$scope.getFeatured = function(searchText, categoryType, id) {	
					var filters = "";
					$('input[name="filter"]:checked').each(function() {
						   filters = filters + this.value + ":";
					});
					var query = "";
					if($('#id').val()){
						query = query + $('#categoryType').val() + ":" + $('#id').val() + "::"; 
					}
					if($('#brandTypeId').val()){
						query = query + $('#brandType').val() + ":" + $('#brandTypeId').val(); 
					}
					$http.get("/shop/newOnSaleImported?searchText="+searchText+"&categoryType="+query+"&filter="+filters).
	    	 	success(function(data, status, headers, config) {
	    	 		$scope.featuredI = data;
		             }).error(function(data, status, headers, config) {
		             });
					};
					
				$scope.itemPaginate = function(searchText, categoryType, id, sortBy, sortOrder, from){
					from = (from-1)*12;
					searchText = $('input#searchText').val();
					var queries = $('.selectpicker').val().split(":");
					sortBy= queries[0];
					sortOrder = queries[1];
					var filters = "";
					$('input[name="filter"]:checked').each(function() {
						   filters = filters + this.value + ":";
					});
					var query = "";
					if($('#id').val()){
						query = query + $('#categoryType').val() + ":" + $('#id').val() + "::"; 
					}
					if($('#brandTypeId').val()){
						query = query + $('#brandType').val() + ":" + $('#brandTypeId').val(); 
					}

					$http.get("/shop/items?searchText="+searchText+"&categoryType="+query+"&filter="+filters+"&sortBy="+sortBy+"&sortOrder="+sortOrder+"&page="+from).
             	 	success(function(data, status, headers, config) {
             	 		$scope.items = data.items;
             	 		$scope.totalPages = data.totalPages;
//            	 		 $('#page-selection').bootpag({
//           		            total: Math.ceil($scope.totalPages/12),
//           		            maxVisible: 10,
//           		            page:1
//           		        }).on("page", function(event, /* page number here */ num){
//        					var queries = $('.selectpicker').val().split(":");
//           		        	console.log(categoryType+"--"+id+"--"+$('#searchText').val()+"--"+searchText);
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
				 
					angular.element('#itemController').scope().getFeatured("","","");
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
				$scope.getCategories = function(searchText,categoryType,id) {
				var filters = "";
				$('input[name="filter"]:checked').each(function() {
					   filters = filters + this.value + ":";
				});
				var query = "";
				if($('#id').val()){
					query = query + $('#categoryType').val() + ":" + $('#id').val() + "::"; 
				}
				if($('#brandTypeId').val()){
					query = query + $('#brandType').val() + ":" + $('#brandTypeId').val(); 
				}
				$http.get("/shop/categories?searchText="+searchText+"&categoryType="+query+"&filter="+filters).
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
				
				$scope.getByCategory = function(id, categoryType, category) {
					var html = "<li id='home'>" + $('ol.breadcrumb li').html() + "</li>";
					var breadcrumb =  "<li id='category'><a href=\"javascript:breadcrumbcall('"+encodeURIComponent(id)+"','"+categoryType+"')\">"+category+"</a></li>";
					$('ol.breadcrumb').html(html+breadcrumb);
					
					var searchText = $('#searchText').val();
					$('#categoryType').val(categoryType);
					$('#id').val(id);
					$('#categoryblock').hide();
					angular.element('#itemController').scope().itemFilter(searchText, categoryType, id);
					
				};
					
				$scope.getBySubCategory = function(id, categoryType, subCategory) {	
					var categoryhtml = $('ol.breadcrumb li#category').html();
					if (typeof categoryhtml === "undefined") {
						categoryhtml = "";
					}else{
						categoryhtml = "<li id='category'>"+categoryhtml+"</li>";
					}
					var html = "<li id='home'>" + $('ol.breadcrumb li#home').html() + "</li>" + categoryhtml;
					var breadcrumb =  "<li id='subcategory'><a href=\"javascript:breadcrumbcall('"+encodeURIComponent(id)+"','subcategories')\">"+subCategory+"</a></li>";
					$('ol.breadcrumb').html(html+breadcrumb);
					
					var searchText = $('#searchText').val();
					$('#categoryType').val(categoryType);
					$('#id').val(id);
					$('#categoryblock').hide();
					$('#subcategoryblock').hide();
					angular.element('#itemController').scope().itemFilter(searchText, categoryType, id);

				};
				
			}); 

 
 shopApp.controller(
			'brandController',
			function($scope, $http, $location) {
				 
					$scope.getBrands = function(searchText,categoryType, id) {
						var filters = "";
						$('input[name="filter"]:checked').each(function() {
							   filters = filters + this.value + ":";
						});
						var query = "";
						if($('#id').val()){
							query = query + $('#categoryType').val() + ":" + $('#id').val() + "::"; 
						}
						if($('#brandTypeId').val()){
							query = query + $('#brandType').val() + ":" + $('#brandTypeId').val(); 
						}
						$http.get("/shop/brands?searchText="+searchText+"&categoryType="+query+"&filter="+filters).
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
					
					$scope.getByBrand = function(id,brandType) {	
						var searchText = $('#searchText').val();
						$('#brandTypeId').val(id);
						$('#brandType').val(brandType);
						angular.element('#itemController').scope().itemFilter(searchText, $('#categoryType').val(), $('#id').val());

					};
				
			}); 
 
 shopApp.controller(
			'productController',
			function($scope, $http, $location) {
				 
					$scope.getProductTypes = function(searchText, categoryType, id) {
						var filters = "";
						$('input[name="filter"]:checked').each(function() {
							   filters = filters + this.value + ":";
						});
						var query = "";
						if($('#id').val()){
							query = query + $('#categoryType').val() + ":" + $('#id').val() + "::"; 
						}
						if($('#brandTypeId').val()){
							query = query + $('#brandType').val() + ":" + $('#brandTypeId').val(); 
						}
						$http.get("/shop/producttypes?searchText="+searchText+"&categoryType="+query+"&filter="+filters).
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
					$scope.getByType = function(id, categoryType, type) {	
						
						var categoryhtml = $('ol.breadcrumb li#category').html();
						if (typeof categoryhtml === "undefined") {
							categoryhtml = "";
						}else{
							categoryhtml = "<li id='category'>" + categoryhtml + "</li>";
						}
						
						var subcategoryhtml = $('ol.breadcrumb li#subcategory').html();
						if (typeof subcategoryhtml === "undefined") {
							subcategoryhtml = "";
						}else{
							subcategoryhtml = "<li id='subcategory'>" + subcategoryhtml + "</li>";
						}
						
						
						var html = "<li id='home'>" + $('ol.breadcrumb li#home').html() + "</li>" + categoryhtml + subcategoryhtml;
						var breadcrumb =  "<li id='product'><a href=\"javascript:breadcrumbcall('"+id+"','"+categoryType+"')\" >"+type+"</a></li>";
						$('ol.breadcrumb').html(html+breadcrumb);
						
						var searchText = $('#searchText').val();
						$('#categoryType').val(categoryType);
						$('#id').val(id);
						$('#categoryblock').hide();
						$('#subcategoryblock').hide();
						$('#productController').hide();
						angular.element('#itemController').scope().itemFilter(searchText, categoryType, id);

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

var configureApp = angular.module('configureApp', ['ngResource','ngRoute']);
configureApp.controller(
		'configureController',
		function($scope, $http, $location) {
			
//			$scope.getByCategory = function(id, categoryType, category) {
//				var html = "<li id='home'>" + $('ol.breadcrumb li').html() + "</li>";
//				var breadcrumb =  "<li id='category'><a href=\"javascript:breadcrumbcall('"+encodeURIComponent(id)+"','"+categoryType+"')\">"+category+"</a></li>";
//				$('ol.breadcrumb').html(html+breadcrumb);
//				
//				var searchText = $('#searchText').val();
//				$('#categoryType').val(categoryType);
//				$('#id').val(id);
//				$('#categoryblock').hide();
//				angular.element('#itemController').scope().itemFilter(searchText, categoryType, id);
//				
//			};
			
			$scope.getCustomKeywords = function() {
				$http.get("/configure/getCustomKeywords").
       	 			success(function(data, status, headers, config) {
       	 				$scope.keywords = data;
	             }).error(function(data, status, headers, config) {
	             });
				};
			
			$scope.addKeyword = function(keyword) {
				$http.get("/configure/addKeyword?categoryFirst="+$('.categoryfirst').val()+"&categorySecond="+$('.categorysecond').val()+"&condition="+$('.conditionselector').val()+"&keyword="+$('#keyword').val()).
       	 			success(function(data, status, headers, config) {
	             }).error(function(data, status, headers, config) {
	             });
				window.location.reload();
				};
				
			$scope.deleteKeyword = function(id) {
				$http.get("/configure/deleteKeyword?id="+id).
       	 			success(function(data, status, headers, config) {
	             }).error(function(data, status, headers, config) {
	             });
				window.location.reload();
				};
			
			$scope.getCategories = function() {
				$http.get("/shop/categories").
       	 			success(function(data, status, headers, config) {
       	 		$scope.categories = data.mainObject[0];

	             }).error(function(data, status, headers, config) {
	             });
				};
			
			angular.element('#configureController').scope().getCategories();
			angular.element('#configureController').scope().getCustomKeywords();


		}); 