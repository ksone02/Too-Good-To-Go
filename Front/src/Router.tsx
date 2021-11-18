import React from "react";
import { BrowserRouter, Route, Switch } from "react-router-dom";
import Header from "./components/organisms/Header/Header";
import Login from "./pages/Login/LoginPage";
import Register from "./pages/Register/RegisterPage";
import Shop from "./pages/Shop/ShopPage";
import ShopMenuPage from "./pages/ShopMenu/ShopMenuPage";
import Footer from "./components/organisms/Footer/Footer";
import CartPage from "./pages/Cart/CartPage";

const Router: React.FC = () => {
	return (
		<BrowserRouter>
			<Header />
			<Switch>
				<Route exact path="/shopmenu/" component={ShopMenuPage} />
				<Route path="/shopmenu/:menuName" component={ShopMenuPage} />
				<Route exact path="/shop" component={Shop} />
				<Route exact path="/login" component={Login} />
				<Route exact path="/register" component={Register} />
				<Route exact path="/cart" component={CartPage} />
				<Route render={() => <div>에러페이지</div>} />
			</Switch>
			<Footer />
		</BrowserRouter>
	);
};

export default Router;
