package com.dtnsbike.controller.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Brands;
import com.dtnsbike.entity.Categories;
import com.dtnsbike.entity.Colors;
import com.dtnsbike.entity.DetailPhotos;
import com.dtnsbike.entity.Discounts;
import com.dtnsbike.entity.OrderDetails;
import com.dtnsbike.entity.Origins;
import com.dtnsbike.entity.ProductDetails;
import com.dtnsbike.entity.Products;
import com.dtnsbike.entity.Sizes;
import com.dtnsbike.model.SizesModel;
import com.dtnsbike.model.ProductDetailsModel;
import com.dtnsbike.model.ProductModel;
import com.dtnsbike.service.ConvertPageService;
import com.dtnsbike.service.FileManagerService;
import com.dtnsbike.service.RestApiService;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/admin")
public class ProductsController {
	String path = "admin/common/products/";

	@Autowired
	ObjectMapper mapper;

	@Autowired
	ConvertPageService pageService;

	@Autowired
	RestApiService api;

	@Autowired
	SessionService session;

	@Autowired
	FileManagerService fileService;

	@Autowired
	AccountsDAO accountDao;

	@Autowired
	HttpServletRequest request;

	@RequestMapping("/product-list.html")
	public String loadListProduct(Model m, HttpServletRequest request, @RequestParam("page") Optional<String> pages)
			throws JsonParseException, JsonMappingException, IOException {
		String sizes = request.getParameter("size");

		if (sizes == null) {
			sizes = "5";
		}
		if (!NumberUtils.isParsable(sizes)) {
			sizes = "5";
		}

		List<Integer> arr = new ArrayList<Integer>(4);
		arr.add(5);
		arr.add(10);
		arr.add(15);
		arr.add(20);
		Integer size = Integer.valueOf(sizes);
		Integer page = 1;
		if (pages.isPresent()) {
			if (NumberUtils.isParsable(String.valueOf(pages.get()))) {
				page = Integer.valueOf(pages.get());
			}
		}
		if (size < 5 || size > 20 || !arr.contains(size)) {
			size = 5;
		}
		TypeReference<List<Products>> typePro = new TypeReference<List<Products>>() {
		};
		List<Products> listPro = mapper.readValue(api.get("/DTNsBike/rest/products").toString(), typePro);
		if (pageService.checkTotalPages(listPro, page, size) == false) {
			page = 1;
		}

		Pageable pageable = PageRequest.of((page - 1), size);
		Integer totalPage = listPro.size() / size;
		if (listPro.size() % size > 0) {
			totalPage = totalPage + 1;
		}
		@SuppressWarnings("unchecked")
		List<Products> products = (List<Products>) pageService.toPage(listPro, pageable).getContent();
		m.addAttribute("listPro", products);
		m.addAttribute("listPage", pageService.listPage(listPro, page, size));
		m.addAttribute("page", page);
		m.addAttribute("size", size);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("totalItems", listPro.size());
		m.addAttribute("pageableItems", pageable);
		return path + "product-list.html";
	}

	@RequestMapping("/product-update.html")
	public String getFormUpdate(@ModelAttribute("productModel") ProductModel productModel, Model m) {
		return path + "product-update.html";
	}

	@RequestMapping("/product/form/edit/{id}")
	public String getFormEdit(@PathVariable("id") Optional<String> id, Model m, HttpServletRequest request,
			@ModelAttribute("productModel") ProductModel productModel)
			throws JsonMappingException, JsonProcessingException, IOException {
		if (!checkTypes(request) || !id.isPresent()) {
			return "redirect:/pagenotfound.html";
		}
		if (request.getParameter("message") != null) {
			if (request.getParameter("message").equals("123")) {
				m.addAttribute("message", "Thêm sản phẩm thành công.");
			} else if (request.getParameter("message").equals("456")) {
				m.addAttribute("message", "Cập nhật thông tin sản phẩm thành công.");
				if (session.get("msgUdt") != null) {
					m.addAttribute("msgUdt", session.get("msgUdt"));

				}
			}
		}
		String paths = new String();
		TypeReference<List<String>> typeLstString = new TypeReference<List<String>>() {
		};
		Products products = new Products();
		if (request.getParameter("editing") != null) {
			if (session.get("productImgLst") == null && !request.getParameter("editing").equals("true")) {
				List<String> listImg = new ArrayList<>();
				paths = "/DTNsBike/rest/photos/pro/" + id.get();
				listImg = mapper.readValue(api.get(paths).toString(), typeLstString);
				session.set("productImgLst", listImg);
			}
		} else {
			List<String> listImg = new ArrayList<>();
			paths = "/DTNsBike/rest/photos/pro/" + id.get();
			listImg = mapper.readValue(api.get(paths).toString(), typeLstString);
			session.set("productImgLst", listImg);
		}
		if (session.get("proUpdate") != null) {
			productModel = session.get("proUpdate");
		} else {
			TypeReference<Products> typePro = new TypeReference<Products>() {
			};
			paths = "/DTNsBike/rest/products/" + id.get();
			if (mapper.readValue(api.get(paths).toString(), typePro) == null) {
				return "redirect:/pagenotfound.html";
			} else {
				products = mapper.readValue(api.get(paths).toString(), typePro);
				session.set("proEdit", products.getId());
				if (!request.getParameter("types").equals(String.valueOf(products.getCatePro().getTypeId().getId()))) {
					return "redirect:/pagenotfound.html";
				}
			}
			productModel.setName(products.getName());
			productModel.setAvaliable(products.getAvaliable());
			productModel.setBrandsID(String.valueOf(products.getBrandPro().getBrandid()));
			productModel.setCategoriesID(String.valueOf(products.getCatePro().getId()));
			productModel.setOriginsID(String.valueOf(products.getOriginid().getId()));
			productModel.setDiscountsID(String.valueOf(products.getDiscountid().getId()));
			Integer price = products.getPrice().intValue();
			productModel.setPrice(price);
			productModel.setVat(products.getVat().intValue());
			productModel.setDescription(products.getDescription());
			productModel.setWarrantyPeriod(products.getWarrantyperiod());
			if (products.getImg() != null) {
				session.set("productImgs", products.getImg());
			} else {
				session.set("productImgs", "default.jpg");
			}
			if (!request.getParameter("types").endsWith("1")) {
				List<ProductDetails> productDetails = new ArrayList<>();
				TypeReference<List<ProductDetails>> typeDetail = new TypeReference<List<ProductDetails>>() {
				};
				paths = "/DTNsBike/rest/productdetails/pro/" + id.get();

				productDetails = mapper.readValue(api.get(paths).toString(), typeDetail);
				productModel.setSl(String.valueOf(productDetails.get(0).getAmount()));
			} else {
				TypeReference<ProductDetails> typeDtl = new TypeReference<ProductDetails>() {
				};
				TypeReference<List<Colors>> typeColor = new TypeReference<List<Colors>>() {
				};
				TypeReference<List<Sizes>> typeSize = new TypeReference<List<Sizes>>() {
				};
				List<Colors> colorsLst = mapper.readValue(api.get("/DTNsBike/rest/colors").toString(), typeColor);
				List<Sizes> sizesLst = mapper.readValue(api.get("/DTNsBike/rest/sizes").toString(), typeSize);

				SizesModel szeMd = new SizesModel();
				List<SizesModel> szeMdLst = new ArrayList<>();
				for (int y = 0; y < sizesLst.size(); y++) {
					szeMd = new SizesModel();
					szeMd.setSize(sizesLst.get(y).getId());
					szeMdLst.add(szeMd);
				}
				ProductDetailsModel proDtlMd = new ProductDetailsModel();
				ProductDetails proDtl = new ProductDetails();
				List<ProductDetailsModel> proDtlMdLst = new ArrayList<>();
				for (int x = 0; x < colorsLst.size(); x++) {
					proDtlMd = new ProductDetailsModel();
					proDtlMd.setColorID(colorsLst.get(x).getId());
					proDtlMd.setSizes(szeMdLst);
					proDtlMdLst.add(proDtlMd);
				}
				ProductDetailsModel proDtlMdResult = new ProductDetailsModel();

				for (int i = 0; i < proDtlMdLst.size(); i++) {
					proDtlMdResult = new ProductDetailsModel();
					String pth = "/DTNsBike/rest/productdetails/proandclr/" + products.getId().toString() + "/"
							+ proDtlMdLst.get(i).getColorID().toString();
					if (api.get(pth).asBoolean()) {
						proDtlMdResult.setColorID(proDtlMdLst.get(i).getColorID());
						List<SizesModel> getListSze = new ArrayList<>();
						for (int j = 0; j < sizesLst.size(); j++) {
							SizesModel sizesModel = new SizesModel();
							pth = "/DTNsBike/rest/productdetails/proandclrandsze/" + products.getId().toString() + "/"
									+ proDtlMdLst.get(i).getColorID().toString() + "/" + sizesLst.get(j).getId();
							proDtl = mapper.readValue(api.get(pth).toString(), typeDtl);
							if (proDtl.getId() == null) {
								sizesModel.setSize(null);
								getListSze.add(sizesModel);
							} else {
								sizesModel.setSize(sizesLst.get(j).getId());
								sizesModel.setSl(String.valueOf(proDtl.getAmount()));
								getListSze.add(sizesModel);
							}
						}
						proDtlMdResult.setSizes(getListSze);// end for j
						proDtlMdLst.set(i, proDtlMdResult);
					} else {
						proDtlMdLst.set(i, null);
					}
				} // end for i
				productModel.setColor(proDtlMdLst);
			}
		}
		m.addAttribute("productModel", productModel);

		session.remove("proUpdate");
		return path + "product-update.html";
	}

	@GetMapping("/product/form/update/{id}")
	public String doGetUpdatePro(Model m, HttpServletRequest request, @PathVariable("id") Optional<String> id,
			@ModelAttribute("productModel") ProductModel productModel)
			throws JsonMappingException, JsonProcessingException, IOException {
		if (!checkTypes(request) || !id.isPresent()) {
			return "redirect:/pagenotfound.html";
		}
		Products products = new Products();
		String paths = new String();
		TypeReference<Products> typePro = new TypeReference<Products>() {
		};
		paths = "/DTNsBike/rest/products/" + id.get();
		if (mapper.readValue(api.get(paths).toString(), typePro) == null) {
			return "redirect:/pagenotfound.html";
		} else {
			products = mapper.readValue(api.get(paths).toString(), typePro);
			session.set("proEdit", products.getId());
			if (!request.getParameter("types").equals(String.valueOf(products.getCatePro().getTypeId().getId()))) {
				return "redirect:/pagenotfound.html";
			}
		}
		return "redirect:/admin/product/form/edit/" + id.get() + "?types="
				+ String.valueOf(products.getCatePro().getTypeId().getId());
	}

	@PostMapping("/product/form/update/{id}")
	public String doPostUpdatePro(Model m, HttpServletRequest request, @PathVariable("id") Optional<String> id,
			@Validated @ModelAttribute("productModel") ProductModel productModel, BindingResult errors)
			throws JsonMappingException, JsonProcessingException, IOException {
		if (!checkTypes(request) || !id.isPresent()) {
			return "redirect:/pagenotfound.html";
		}

		Products products = new Products();
		String paths = new String();
		TypeReference<Products> typePro = new TypeReference<Products>() {
		};
		paths = "/DTNsBike/rest/products/" + id.get();
		if (mapper.readValue(api.get(paths).toString(), typePro) == null) {
			return "redirect:/pagenotfound.html";
		} else {
			products = mapper.readValue(api.get(paths).toString(), typePro);
			session.set("proEdit", products.getId());
			if (!request.getParameter("types").equals(String.valueOf(products.getCatePro().getTypeId().getId()))) {
				return "redirect:/pagenotfound.html";
			}
		}
		List<ProductDetailsModel> getProDetailsContronller = productModel.getColor();

		ProductDetailsModel productDetailsModelTemp = new ProductDetailsModel();
		SizesModel sizesModelTemp = new SizesModel();
		List<SizesModel> listSizeTemp = new ArrayList<>();

		List<ProductDetailsModel> resultDetailsModels = new ArrayList<>();
		if (request.getParameter("types").equals("1")) {
			if (productModel.getColor() == null) {
				return "redirect:/admin/index.html";
			}
			Integer xx = 0, yy = 0;
			for (int i = 0; i < getProDetailsContronller.size(); i++) {
				yy = 0;
				productDetailsModelTemp = new ProductDetailsModel();
				if (getProDetailsContronller.get(i).getColorID() != null) {
					productDetailsModelTemp.setColorID(getProDetailsContronller.get(i).getColorID());
					listSizeTemp = new ArrayList<>();
					for (int j = 0; j < getProDetailsContronller.get(i).getSizes().size(); j++) {
						sizesModelTemp = new SizesModel();
						if (getProDetailsContronller.get(i).getSizes().get(j).getSize() != null) {
							sizesModelTemp.setSize(getProDetailsContronller.get(i).getSizes().get(j).getSize());
							if (getProDetailsContronller.get(i).getSizes().get(j).getSl() != null) {
								if (NumberUtils.isParsable(
										String.valueOf(getProDetailsContronller.get(i).getSizes().get(j).getSl()))) {
									sizesModelTemp.setSl(getProDetailsContronller.get(i).getSizes().get(j).getSl());
									listSizeTemp.add(sizesModelTemp);
								} else {
									yy++;
									errors.rejectValue("color[" + i + "].sizes[" + j + "].sl", "error.productmodel",
											"*Vui lòng nhập số lượng.");
								}
							} else {
								yy++;
								errors.rejectValue("color[" + i + "].sizes[" + j + "].sl", "error.productmodel",
										"*Vui lòng nhập số lượng.");
							}
						}
					}
					if (listSizeTemp.size() > 0) {
						productDetailsModelTemp.setSizes(listSizeTemp);
						resultDetailsModels.add(productDetailsModelTemp);
					} else {
						if (yy == 0) {
							xx++;
							errors.rejectValue("color[" + i + "].colorID", "error.productmodel",
									"*Vui lòng ít nhất một size.");
						} else {
							xx++;
							errors.rejectValue("color[" + i + "].colorID", "error.productmodel",
									"*Kiểm tra lại số lượng của size.");
						}
					}
				}
			}
			if (xx == 0 && yy == 0) {
				if (resultDetailsModels.size() == 0) {
					errors.rejectValue("color", "error.productmodel", "*Vui lòng ít nhất một màu.");
				}
			} else {
				errors.rejectValue("color", "error.productmodel", "*Kiểm tra lại size và số lượng.");
			}
		} else {
			if (productModel.getSl() == null) {
				errors.rejectValue("sl", "error.productmodel", "*Vui lòng nhập số lượng sản phẩm.");
			} else {
				if (!NumberUtils.isParsable(productModel.getSl())) {
					errors.rejectValue("sl", "error.productmodel", "*Vui lòng đúng định dạng số.");
				}
			}
		}
		if (!errors.hasErrors()) {
			Integer error = 0;
			String cateID = productModel.getCategoriesID();
			String discountID = productModel.getDiscountsID();
			String brandID = productModel.getBrandsID();
			String originID = productModel.getOriginsID();
			paths = new String();

			// Get cate
			Categories categories = new Categories();
			TypeReference<Categories> typeCate = new TypeReference<Categories>() {
			};
			paths = "/DTNsBike/rest/categories/" + cateID;

			if (mapper.readValue(api.get(paths).toString(), typeCate) == null) {
				error++;
			} else {
				categories = mapper.readValue(api.get(paths).toString(), typeCate);
				if (categories.getTypeId().getId() != Integer.valueOf(request.getParameter("types"))) {
					error++;
				}
			}

			// Get Discount
			Discounts discounts = new Discounts();
			TypeReference<Discounts> typeDis = new TypeReference<Discounts>() {
			};
			paths = "/DTNsBike/rest/discounts/" + discountID;

			if (mapper.readValue(api.get(paths).toString(), typeDis) == null) {
				error++;
			} else {
				discounts = mapper.readValue(api.get(paths).toString(), typeDis);
			}

			// Get Brands
			Brands brands = new Brands();
			TypeReference<Brands> typeBra = new TypeReference<Brands>() {
			};
			paths = "/DTNsBike/rest/brands/" + brandID;

			if (mapper.readValue(api.get(paths).toString(), typeBra) == null) {
				error++;
			} else {
				brands = mapper.readValue(api.get(paths).toString(), typeBra);
			}

			// Get Brands
			Origins origins = new Origins();
			TypeReference<Origins> typeOri = new TypeReference<Origins>() {
			};
			paths = "/DTNsBike/rest/origins/" + originID;

			if (mapper.readValue(api.get(paths).toString(), typeOri) != null) {
				origins = mapper.readValue(api.get(paths).toString(), typeOri);

			} else {
				error++;
			}
			if (error == 0) {
				List<String> message = new ArrayList<>();
				session.remove("msgUdt");
				products.setName(productModel.getName());
				String img = session.get("productImgs");
				if (img != null) {
					products.setImg(img);
				} else {
					products.setImg("default.jpg");
				}
				products.setAvaliable(productModel.getAvaliable());
				products.setPrice(Double.valueOf(productModel.getPrice()));
				products.setVat(Double.valueOf(productModel.getVat()));
				products.setWarrantyperiod(Integer.valueOf(productModel.getWarrantyPeriod()));
				products.setDescription(productModel.getDescription());
				products.setCatePro(categories);
				products.setBrandPro(brands);
				products.setDiscountid(discounts);
				products.setOriginid(origins);
				api.push("/DTNsBike/rest/products/" + id.get(), products);
				if (request.getParameter("types").equals("1") && products.getCatePro().getTypeId().getId() == 1) {
					if (resultDetailsModels.size() > 0) {
						List<String> lstStc = new ArrayList<>();// List tổng hợp (list synthetic);
						List<String> lstStc1 = new ArrayList<>();
						List<String> lstStc2 = new ArrayList<>();
						List<String> lstStc3 = new ArrayList<>();
						List<String> lstDtb = new ArrayList<>();// List tổng hợp từ database (list database);
						List<String> lstDtb1 = new ArrayList<>();
						List<String> lstDtb2 = new ArrayList<>();
						List<String> lstDtb3 = new ArrayList<>();
						List<String> lstDlt = new ArrayList<>();// List màu size cần xóa (list delete);
						List<String> lstUdt = new ArrayList<>();// List màu size cần update (list update);
						List<String> lstIst = new ArrayList<>();// List màu size cần thêm mới (list insert).
						// Set lstStc
						for (int i = 0; i < resultDetailsModels.size(); i++) {
							String color = resultDetailsModels.get(i).getColorID();
							for (int j = 0; j < resultDetailsModels.get(i).getSizes().size(); j++) {
								String size = resultDetailsModels.get(i).getSizes().get(j).getSize();
								lstStc.add(color + "_" + size);
								lstStc1.add(color + "_" + size);
								lstStc2.add(color + "_" + size);
								lstStc3.add(color + "_" + size);
							}
						}
						List<ProductDetails> lstProDtl = new ArrayList<>();
						TypeReference<List<ProductDetails>> typeProDtlLst = new TypeReference<List<ProductDetails>>() {
						};
						TypeReference<ProductDetails> typeProDtl = new TypeReference<ProductDetails>() {
						};
						TypeReference<List<OrderDetails>> typeOdrDltLst = new TypeReference<List<OrderDetails>>() {
						};
						if (api.get("/DTNsBike/rest/productdetails/pro/" + id.get()).asToken() != null) {
							// Set lstProDtl
							lstProDtl = mapper.readValue(
									api.get("/DTNsBike/rest/productdetails/pro/" + id.get()).toString(), typeProDtlLst);
							// Set lstDtb
							for (int i = 0; i < lstProDtl.size(); i++) {
								String color = lstProDtl.get(i).getColorid().getId();
								String size = lstProDtl.get(i).getSizeid().getId();
								lstDtb.add(color + "_" + size);
								lstDtb1.add(color + "_" + size);
								lstDtb2.add(color + "_" + size);
								lstDtb3.add(color + "_" + size);
							}

							lstIst = lstStc1;
							lstIst.removeAll(lstDtb1);
							// Insert prodetails
							for (int i = 0; i < lstIst.size(); i++) {
								String colorID = lstIst.get(i).substring(0, 6);
								String sizeID = lstIst.get(i).substring(7, lstIst.get(i).length());
								String sl = new String();
								for (int j = 0; j < resultDetailsModels.size(); j++) {
									if (resultDetailsModels.get(j).getColorID().equals(colorID)) {
										for (int x = 0; x < resultDetailsModels.get(j).getSizes().size(); x++) {
											if (resultDetailsModels.get(j).getSizes().get(x).getSize().equals(sizeID)) {
												sl = resultDetailsModels.get(j).getSizes().get(x).getSl();
											}
										}
									}
								}
								TypeReference<Colors> typeColor = new TypeReference<Colors>() {
								};
								TypeReference<Sizes> typeSize = new TypeReference<Sizes>() {
								};

								ProductDetails pdtDtl = new ProductDetails();
								paths = "/DTNsBike/rest/colors/" + colorID;
								pdtDtl.setColorid(mapper.readValue(api.get(paths).toString(), typeColor));
								pdtDtl.setProductid(products);
								paths = "/DTNsBike/rest/sizes/" + sizeID;
								pdtDtl.setSizeid(mapper.readValue(api.get(paths).toString(), typeSize));
								pdtDtl.setAmount(Integer.valueOf(sl));
								api.post("/DTNsBike/rest/productDetails", pdtDtl);
							}

							lstUdt = lstStc2;
							lstUdt.retainAll(lstDtb2);
							for (int i = 0; i < lstUdt.size(); i++) {
								String colorID = lstUdt.get(i).substring(0, 6);
								String sizeID = lstUdt.get(i).substring(7, lstUdt.get(i).length());
								String sl = new String();
								for (int j = 0; j < resultDetailsModels.size(); j++) {
									if (resultDetailsModels.get(j).getColorID().equals(colorID)) {
										for (int x = 0; x < resultDetailsModels.get(j).getSizes().size(); x++) {
											if (resultDetailsModels.get(j).getSizes().get(x).getSize().equals(sizeID)) {
												sl = resultDetailsModels.get(j).getSizes().get(x).getSl();
											}
										}
									}
								}
								ProductDetails pdtDtl = new ProductDetails();
								String pth = "/DTNsBike/rest/productdetails/proandclrandszes/" + id.get() + "/"
										+ colorID + "/" + sizeID;
								pdtDtl = mapper.readValue(api.get(pth).toString(), typeProDtl);
								if (pdtDtl != null) {
									pdtDtl.setAmount(Integer.valueOf(sl));
									api.push("/DTNsBike/rest/productDetails/" + pdtDtl.getId(), pdtDtl);
								}
							}

							lstDlt = lstDtb3;
							lstDlt.removeAll(lstStc3);
							for (int i = 0; i < lstDlt.size(); i++) {
								String colorID = lstDlt.get(i).substring(0, 6);
								String sizeID = lstDlt.get(i).substring(7, lstDlt.get(i).length());

								ProductDetails pdtDtl = new ProductDetails();
								List<OrderDetails> odrDtlLst = new ArrayList<>();
								String pth = "/DTNsBike/rest/productdetails/proandclrandszes/" + id.get() + "/"
										+ colorID + "/" + sizeID;
								pdtDtl = mapper.readValue(api.get(pth).toString(), typeProDtl);
								pth = "/DTNsBike/rest/orderdetails/prodetaiid/" + pdtDtl.getId();
								odrDtlLst = mapper.readValue(api.get(pth).toString(), typeOdrDltLst);
								if (odrDtlLst.size() == 0) {
									api.delete("/DTNsBike/rest/productDetails/" + pdtDtl.getId());

								} else {
									message.add("Màu " + pdtDtl.getColorid().getName() + " và size "
											+ pdtDtl.getSizeid().getId() + " SP này đã được tạo HĐ không thể xóa.");
									session.set("msgUdt", message);
								}
							}

						}
					}
				} else {
					TypeReference<List<ProductDetails>> typeProDtlLst = new TypeReference<List<ProductDetails>>() {
					};
					List<ProductDetails> pdtDtlLst = new ArrayList<>();
					String pth = "/DTNsBike/rest/productdetails/pro/" + id.get();
					pdtDtlLst = mapper.readValue(api.get(pth).toString(), typeProDtlLst);
					if (pdtDtlLst.size() > 0) {
						ProductDetails pdtDtl = pdtDtlLst.get(0);
						pdtDtl.setAmount(Integer.valueOf(productModel.getSl()));
						api.push("/DTNsBike/rest/productDetails/" + pdtDtl.getId(), pdtDtl);
					}
				}
				TypeReference<List<String>> typeLstString = new TypeReference<List<String>>() {
				};
				List<String> lstImgInData = new ArrayList<>();
				List<String> lstImgInCtl = new ArrayList<>();
				paths = "/DTNsBike/rest/photos/pro/" + id.get();
				lstImgInData = mapper.readValue(api.get(paths).toString(), typeLstString);
				lstImgInCtl = session.get("productImgLst");
				TypeReference<DetailPhotos> typeDltPht = new TypeReference<DetailPhotos>() {
				};
				if (lstImgInData.size() > 0 && lstImgInCtl == null || lstImgInCtl.size() == 0) {
					for (int i = 0; i < lstImgInData.size(); i++) {
						DetailPhotos detailsPhoto = new DetailPhotos();
						if (api.get("/DTNsBike/rest/detail/product/photos?product=" + products.getId() + "&filename="
								+ lstImgInData.get(i)).toString() != null) {
							detailsPhoto = mapper.readValue(api.get("/DTNsBike/rest/detail/product/photos?product="
									+ products.getId() + "&filename=" + lstImgInData.get(i)).toString(), typeDltPht);
							api.delete("/DTNsBike/rest/detailphotos/delete/" + detailsPhoto.getId());
							fileService.delete("products_img", detailsPhoto.getPhotoname());
						}
					}
				} else {
					List<?> lstImgDt1 = lstImgInData.stream().map(map -> map.intern()).collect(Collectors.toList());
					List<?> lstImgCl1 = lstImgInCtl.stream().map(map -> map.intern()).collect(Collectors.toList());
					lstImgCl1.removeAll(lstImgDt1);
					List<?> lstIst = lstImgCl1;
					// Insert hình mới
					if (lstIst != null) {
						if (lstIst.size() > 0) {
							for (int i = 0; i < lstIst.size(); i++) {
								DetailPhotos detailsPhoto = new DetailPhotos();
								detailsPhoto.setProductid(products);
								detailsPhoto.setPhotoname(String.valueOf(lstIst.get(i)));
								api.post("/DTNsBike/rest/detailPhotos", detailsPhoto);
							}
						}
					}
					List<?> lstImgDt2 = lstImgInData.stream().map(map -> map.intern()).collect(Collectors.toList());
					List<?> lstImgCl2 = lstImgInCtl.stream().map(map -> map.intern()).collect(Collectors.toList());
					lstImgDt2.removeAll(lstImgCl2);
					List<?> lstDlt = lstImgDt2;
					// Delete ảnh chi tiết
					if (lstDlt != null) {
						if (lstDlt.size() > 0) {
							for (int i = 0; i < lstDlt.size(); i++) {
								DetailPhotos detailsPhoto = new DetailPhotos();
								String pth = new String();
								pth = "/DTNsBike/rest/detail/product/photos?product=" + products.getId() + "&filename="
										+ lstDlt.get(i);
								if (api.get(pth).toString() != null) {
									if (!api.get(pth).toString().isEmpty()) {
										detailsPhoto = mapper.readValue(api.get(pth).toString(), typeDltPht);
										api.delete("/DTNsBike/rest/detailphotos/delete/" + detailsPhoto.getId());
										fileService.delete("products_img", detailsPhoto.getPhotoname());
									}
								}

							}
						}
					}
				}
				return "redirect:/admin/product/form/edit/" + id.get() + "?types=" + request.getParameter("types")
						+ "&message=456";
			}
		}
		session.remove("proUpdate");
		session.remove("productImgLst");
		return path + "product-update.html";
	}

	@RequestMapping("/product/form/add")
	public String postFormAdd(@Validated @ModelAttribute("productModel") ProductModel productModel,
			BindingResult errors, Model m, HttpServletRequest request)
			throws JsonMappingException, JsonProcessingException, IOException {
		if (!checkTypes(request)) {
			return "redirect:/pagenotfound.html";
		}
		if (session.get("proInsert") != null) {
			productModel = session.get("proInsert");
			m.addAttribute("productModel", productModel);
		}
		session.remove("proInsert");

		List<ProductDetailsModel> getProDetailsContronller = productModel.getColor();

		ProductDetailsModel productDetailsModelTemp = new ProductDetailsModel();
		SizesModel sizesModelTemp = new SizesModel();
		List<SizesModel> listSizeTemp = new ArrayList<>();

		List<ProductDetailsModel> resultDetailsModels = new ArrayList<>();

		if (request.getParameter("types").equals("1")) {
			if (productModel.getColor() == null) {
				return "redirect:/admin/index.html";
			}
			Integer xx = 0, yy = 0;
			for (int i = 0; i < getProDetailsContronller.size(); i++) {
				yy = 0;
				productDetailsModelTemp = new ProductDetailsModel();
				if (getProDetailsContronller.get(i).getColorID() != null) {
					productDetailsModelTemp.setColorID(getProDetailsContronller.get(i).getColorID());
					listSizeTemp = new ArrayList<>();
					for (int j = 0; j < getProDetailsContronller.get(i).getSizes().size(); j++) {
						sizesModelTemp = new SizesModel();
						if (getProDetailsContronller.get(i).getSizes().get(j).getSize() != null) {
							sizesModelTemp.setSize(getProDetailsContronller.get(i).getSizes().get(j).getSize());
							if (getProDetailsContronller.get(i).getSizes().get(j).getSl() != null) {
								if (NumberUtils.isParsable(
										String.valueOf(getProDetailsContronller.get(i).getSizes().get(j).getSl()))) {
									sizesModelTemp.setSl(getProDetailsContronller.get(i).getSizes().get(j).getSl());
									listSizeTemp.add(sizesModelTemp);
								} else {
									yy++;
									errors.rejectValue("color[" + i + "].sizes[" + j + "].sl", "error.productmodel",
											"*Vui lòng nhập số lượng.");
								}
							} else {
								yy++;
								errors.rejectValue("color[" + i + "].sizes[" + j + "].sl", "error.productmodel",
										"*Vui lòng nhập số lượng.");
							}
						}
					}
					if (listSizeTemp.size() > 0) {
						productDetailsModelTemp.setSizes(listSizeTemp);
						resultDetailsModels.add(productDetailsModelTemp);
					} else {
						if (yy == 0) {
							xx++;
							errors.rejectValue("color[" + i + "].colorID", "error.productmodel",
									"*Vui lòng ít nhất một size.");
						} else {
							xx++;
							errors.rejectValue("color[" + i + "].colorID", "error.productmodel",
									"*Kiểm tra lại số lượng của size.");
						}
					}
				}
			}
			if (xx == 0 && yy == 0) {
				if (resultDetailsModels.size() == 0) {
					errors.rejectValue("color", "error.productmodel", "*Vui lòng ít nhất một màu.");
				}
			} else {
				errors.rejectValue("color", "error.productmodel", "*Kiểm tra lại size và số lượng.");
			}
		} else {
			if (productModel.getSl() == null) {
				errors.rejectValue("sl", "error.productmodel", "*Vui lòng nhập số lượng sản phẩm.");
			} else {
				if (!NumberUtils.isParsable(productModel.getSl())) {
					errors.rejectValue("sl", "error.productmodel", "*Vui lòng đúng định dạng số.");
				}
			}
		}
		if (!errors.hasErrors()) {
			Integer error = 0;
			String cateID = productModel.getCategoriesID();
			String discountID = productModel.getDiscountsID();
			String brandID = productModel.getBrandsID();
			String originID = productModel.getOriginsID();
			String paths = new String();

			// Get cate
			Categories categories = new Categories();
			TypeReference<Categories> typeCate = new TypeReference<Categories>() {
			};
			paths = "/DTNsBike/rest/categories/" + cateID;

			if (mapper.readValue(api.get(paths).toString(), typeCate) == null) {
				error++;
			} else {
				categories = mapper.readValue(api.get(paths).toString(), typeCate);
				if (categories.getTypeId().getId() != Integer.valueOf(request.getParameter("types"))) {
					error++;
				}
			}

			// Get Discount
			Discounts discounts = new Discounts();
			TypeReference<Discounts> typeDis = new TypeReference<Discounts>() {
			};
			paths = "/DTNsBike/rest/discounts/" + discountID;

			if (mapper.readValue(api.get(paths).toString(), typeDis) == null) {
				error++;
			} else {
				discounts = mapper.readValue(api.get(paths).toString(), typeDis);
			}

			// Get Brands
			Brands brands = new Brands();
			TypeReference<Brands> typeBra = new TypeReference<Brands>() {
			};
			paths = "/DTNsBike/rest/brands/" + brandID;

			if (mapper.readValue(api.get(paths).toString(), typeBra) == null) {
				error++;
			} else {
				brands = mapper.readValue(api.get(paths).toString(), typeBra);
			}

			// Get Brands
			Origins origins = new Origins();
			TypeReference<Origins> typeOri = new TypeReference<Origins>() {
			};
			paths = "/DTNsBike/rest/origins/" + originID;

			if (mapper.readValue(api.get(paths).toString(), typeOri) != null) {
				origins = mapper.readValue(api.get(paths).toString(), typeOri);

			} else {
				error++;
			}
			if (error == 0) {
				Products products = new Products();
				products.setName(productModel.getName());
				String img = session.get("productImgs");
				if (img != null) {
					products.setImg(img);
				} else {
					products.setImg("default.jpg");
				}
				products.setAvaliable(productModel.getAvaliable());
				products.setPrice(Double.valueOf(productModel.getPrice()));
				products.setVat(Double.valueOf(productModel.getVat()));
				products.setWarrantyperiod(Integer.valueOf(productModel.getWarrantyPeriod()));
				products.setDescription(productModel.getDescription());
				products.setCatePro(categories);
				products.setBrandPro(brands);
				products.setDiscountid(discounts);
				products.setOriginid(origins);
				TypeReference<Products> typePro = new TypeReference<Products>() {
				};
				Products productsNew = mapper.readValue(api.post("/DTNsBike/rest/products", products).toString(),
						typePro);
				TypeReference<Colors> typeColor = new TypeReference<Colors>() {
				};
				TypeReference<Sizes> typeSize = new TypeReference<Sizes>() {
				};

				if (resultDetailsModels.size() > 0) {
					for (int i = 0; i < resultDetailsModels.size(); i++) {
						ProductDetails productDetails = new ProductDetails();
						Colors colors = new Colors();
						String colorID = resultDetailsModels.get(i).getColorID();
						paths = "/DTNsBike/rest/colors/" + colorID;
						if (mapper.readValue(api.get(paths).toString(), typeColor) != null) {
							colors = mapper.readValue(api.get(paths).toString(), typeColor);
							for (int j = 0; j < resultDetailsModels.get(i).getSizes().size(); j++) {
								String sizeID = resultDetailsModels.get(i).getSizes().get(j).getSize();
								paths = "/DTNsBike/rest/sizes/" + sizeID;
								Sizes sizes = new Sizes();
								if (mapper.readValue(api.get(paths).toString(), typeSize) != null) {
									sizes = mapper.readValue(api.get(paths).toString(), typeSize);
									productDetails.setProductid(productsNew);
									productDetails.setSizeid(sizes);
									productDetails.setColorid(colors);
									productDetails.setAmount(
											Integer.valueOf(resultDetailsModels.get(i).getSizes().get(j).getSl()));
									api.post("/DTNsBike/rest/productDetails", productDetails);
								}
							}
						}

					}
				} else {
					ProductDetails productDetails = new ProductDetails();
					productDetails.setProductid(productsNew);
					productDetails.setSizeid(null);
					productDetails.setColorid(null);
					productDetails.setAmount(Integer.valueOf(Integer.valueOf(productModel.getSl())));
					api.post("/DTNsBike/rest/productDetails", productDetails);
				}
				List<String> imgs = session.get("productImgLst");
				if (imgs != null) {
					if (imgs.size() > 0) {
						for (int i = 0; i < imgs.size(); i++) {
							DetailPhotos detailsPhoto = new DetailPhotos();
							detailsPhoto.setProductid(productsNew);
							detailsPhoto.setPhotoname(imgs.get(i));
							api.post("/DTNsBike/rest/detailPhotos", detailsPhoto);
						}
						session.remove("productImgLst");
					}
				}
				return "redirect:/admin/product/form/edit/" + productsNew.getId() + "?types="
						+ request.getParameter("types") + "&message=123";
			}
		}
		return path + "product-update.html";
	}

	@RequestMapping("/product/form/reset")
	public String doReset(@ModelAttribute("productModel") ProductModel productModel, Model m,
			HttpServletRequest request) {
		productModel = new ProductModel();
		m.addAttribute("productModel", productModel);
		if (request.getParameter("message") != null) {
			if (request.getParameter("message").equals("123")) {
				m.addAttribute("message", "Thêm thành công sản phẩm!");
			}
		}
		session.remove("productImgLst");
		session.remove("proUpdate");
		session.remove("productImgs");
		session.remove("proEdit");
		session.remove("proInsert");
		return path + "product-update.html";
	}

	@PostMapping("/product/upload/img/insert")
	public String postUploadSingeIs(@ModelAttribute("productModel") ProductModel productModel, Model m,
			HttpServletResponse response, RedirectAttributes ra, HttpServletRequest request) throws IOException {
		File file = fileService.save("product_single_img", productModel.getFile(), null, ra);
		if (file == null) {
			return "redirect:/admin/product/form/add?types=" + request.getParameter("types");
		}
		session.set("proInsert", productModel);
		session.set("productImgs", file.getName());
		return "redirect:/admin/product/form/add?types=" + request.getParameter("types");
	}

	@GetMapping("/product/upload/img/insert")
	public String getUploadSingelIs(@ModelAttribute("productModel") ProductModel productModel, Model m,
			HttpServletResponse response, RedirectAttributes ra, HttpServletRequest request) throws IOException {
		return "redirect:/admin/product/form/add?types=" + request.getParameter("types");
	}

	@PostMapping("/product/upload/img/insert/details")
	public String postUploadIs(@ModelAttribute("productModel") ProductModel productModel, Model m,
			HttpServletResponse response, RedirectAttributes ra, HttpServletRequest request) throws IOException {
		File file = fileService.save("products_img", productModel.getFile2(), null, ra);
		if (file == null) {
			return "forward:/admin/product/form/add?types=" + request.getParameter("types");
		}
		session.set("proInsert", productModel);
		List<String> productImgLst = new ArrayList<>();
		if (session.get("productImgLst") != null) {
			productImgLst = session.get("productImgLst");
		}
		productImgLst.add(file.getName());
		session.set("productImgLst", productImgLst);
		return "redirect:/admin/product/form/add?types=" + request.getParameter("types");
	}

	@GetMapping("/product/upload/img/insert/details")
	public String getUploadIs(@ModelAttribute("productModel") ProductModel productModel, Model m,
			HttpServletResponse response, RedirectAttributes ra, HttpServletRequest request) throws IOException {
		return "redirect:/admin/product/form/add?types=" + request.getParameter("types");
	}

	@PostMapping("/product/upload/img/delete/add")
	public String postDeleteuUloadIs(@ModelAttribute("productModel") ProductModel productModel, Model m,
			HttpServletResponse response, RedirectAttributes ra, HttpServletRequest request,
			@RequestParam("imgs") String imgs) throws IOException {
		session.set("proInsert", productModel);
		List<String> productImgLst = new ArrayList<>();
		if (session.get("productImgLst") != null) {
			productImgLst = session.get("productImgLst");
		}
		productImgLst.remove(imgs);
		fileService.delete("products_img", imgs);
		session.set("productImgLst", productImgLst);
		return "redirect:/admin/product/form/add?types=" + request.getParameter("types");
	}

	@PostMapping("/product/upload/img/details/{id}")
	public String postUploadUd(@ModelAttribute("productModel") ProductModel productModel, Model m,
			HttpServletResponse response, RedirectAttributes ra, HttpServletRequest request,
			@PathVariable("id") String id) throws IOException {
		File file = fileService.save("products_img", productModel.getFile2(), null, ra);
		if (file == null) {
			return "redirect:/admin/product/form/add?types=" + request.getParameter("types");
		}
		session.set("proUpdate", productModel);
		List<String> productImgLst = new ArrayList<>();
		if (session.get("productImgLst") != null) {
			productImgLst = session.get("productImgLst");
		}
		productImgLst.add(file.getName());
		session.set("productImgLst", productImgLst);
		return "redirect:/admin/product/form/edit/" + id + "?types=" + request.getParameter("types") + "&editing=true";
	}

	@GetMapping("/product/upload/img/delete/add")
	public String getDeleteuUloadIs(@ModelAttribute("productModel") ProductModel productModel, Model m,
			HttpServletResponse response, RedirectAttributes ra, HttpServletRequest request) throws IOException {
		return "redirect:/admin/product/form/add?types=" + request.getParameter("types");
	}

	@PostMapping("/product/upload/img/{id}")
	public String postUploadSingeUd(@ModelAttribute("productModel") ProductModel productModel, Model m,
			HttpServletResponse response, RedirectAttributes ra, HttpServletRequest request,
			@PathVariable("id") String id) throws IOException {
		File file = fileService.save("product_single_img", productModel.getFile(), null, ra);
		if (file == null) {
			return "redirect:/admin/product/form/edit/" + id + "?types=" + request.getParameter("types")
					+ "&editing=true";
		}
		session.set("proUpdate", productModel);
		session.set("productImgs", file.getName());
		return "redirect:/admin/product/form/edit/" + id + "?types=" + request.getParameter("types") + "&editing=true";
	}

	@PostMapping("/product/upload/img/delete/update/{id}")
	public String postDeleteuUloadDl(@ModelAttribute("productModel") ProductModel productModel, Model m,
			HttpServletResponse response, RedirectAttributes ra, HttpServletRequest request,
			@RequestParam("imgs") String imgs, @PathVariable("id") String idPro) throws IOException {
		session.set("proUpdate", productModel);
		List<String> productImgLst = new ArrayList<>();
		if (session.get("productImgLst") != null) {
			productImgLst = session.get("productImgLst");
		}
		productImgLst.remove(imgs);
		session.set("productImgLst", productImgLst);
		return "redirect:/admin/product/form/edit/" + idPro + "?types=" + request.getParameter("types")
				+ "&editing=true";
	}

	@ModelAttribute("listCategories1")
	public List<Categories> getListCateType1() throws JsonMappingException, JsonProcessingException, IOException {
		List<Categories> list = new ArrayList<>();
		TypeReference<List<Categories>> type = new TypeReference<List<Categories>>() {
		};
		list = mapper.readValue(api.get("/DTNsBike/rest/categories/types/1").toString(), type);
		return list;
	}

	@ModelAttribute("listCategories2")
	public List<Categories> getListCateType2() throws JsonMappingException, JsonProcessingException, IOException {
		List<Categories> list = new ArrayList<>();
		TypeReference<List<Categories>> type = new TypeReference<List<Categories>>() {
		};
		list = mapper.readValue(api.get("/DTNsBike/rest/categories/types/2").toString(), type);
		return list;
	}

	@ModelAttribute("listCategories3")
	public List<Categories> getListCateType3() throws JsonMappingException, JsonProcessingException, IOException {
		List<Categories> list = new ArrayList<>();
		TypeReference<List<Categories>> type = new TypeReference<List<Categories>>() {
		};
		list = mapper.readValue(api.get("/DTNsBike/rest/categories/types/3").toString(), type);
		return list;
	}

	@ModelAttribute("listBrands")
	public List<Brands> getListBrands() throws JsonMappingException, JsonProcessingException, IOException {
		List<Brands> list = new ArrayList<>();
		TypeReference<List<Brands>> type = new TypeReference<List<Brands>>() {
		};
		list = mapper.readValue(api.get("/DTNsBike/rest/brands").toString(), type);
		return list;
	}

	@ModelAttribute("listOrigins")
	public List<Origins> getListOrigins() throws JsonMappingException, JsonProcessingException, IOException {
		List<Origins> list = new ArrayList<>();
		TypeReference<List<Origins>> type = new TypeReference<List<Origins>>() {
		};
		list = mapper.readValue(api.get("/DTNsBike/rest/origins").toString(), type);
		return list;
	}

	@ModelAttribute("listDiscounts")
	public List<Discounts> getListDiscounts() throws JsonMappingException, JsonProcessingException, IOException {
		List<Discounts> list = new ArrayList<>();
		TypeReference<List<Discounts>> type = new TypeReference<List<Discounts>>() {
		};
		list = mapper.readValue(api.get("/DTNsBike/rest/discounts").toString(), type);
		return list;
	}

	public Boolean checkTypes(HttpServletRequest request) {
		Boolean check = false;
		String types = request.getParameter("types");
		if (types != null) {
			if (types.equals("1") || types.equals("2") || types.equals("3")) {
				check = true;
			}
		}
		return check;
	}

	@ModelAttribute("listColors")
	public List<Colors> getListColors() throws JsonMappingException, JsonProcessingException, IOException {
		List<Colors> list = new ArrayList<>();
		TypeReference<List<Colors>> type = new TypeReference<List<Colors>>() {
		};
		list = mapper.readValue(api.get("/DTNsBike/rest/colors").toString(), type);
		return list;
	}

	@ModelAttribute("listSizes")
	public List<Sizes> getListSizes() throws JsonMappingException, JsonProcessingException, IOException {
		List<Sizes> list = new ArrayList<>();
		TypeReference<List<Sizes>> type = new TypeReference<List<Sizes>>() {
		};
		list = mapper.readValue(api.get("/DTNsBike/rest/sizes").toString(), type);
		return list;
	}

	@ModelAttribute("accountProfile")
	public Accounts getListProfile() throws JsonMappingException, JsonProcessingException, IOException {
		return accountDao.findById(request.getUserPrincipal().getName()).get();
	}

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) throws JsonMappingException, JsonProcessingException, IOException {
		dataBinder.setAutoGrowCollectionLimit(getListColors().size());
	}
	@ModelAttribute("check_role")
	Boolean checkRole() {
		Accounts acc = accountDao.findById(request.getUserPrincipal().getName()).get();
		return accountDao.check(acc.getUsername()) != null;
	}
}
