import React, { useEffect, useState } from "react";
import styled from "@emotion/styled";
import { Link, useHistory } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import axios from "axios";
import ErrorModal from "../../components/atoms/Modal/LoginErrorModal";
import { changeField, initializeForm } from "../../modules/auth";
import { tempSetUser } from "../../modules/user";
import { RootState } from "../../app/store";
import { setAccessToken } from "../../helpers/tokenControl";

const LOGIN_URL = "http://54.180.134.20/api"; // http 붙여야함 (404 오류 방지)
const JWT_EXPIREY_TIME = 24 * 3600 * 1000; // 만료시간 (24시간 밀리 초로 표현)

const Login: React.FC = () => {
	const [inputId, setInputId] = useState("");
	const [inputPw, setInputPw] = useState("");
	const [errorModal, setErrorModal] = useState<boolean>(false);
	const [errorMessage, setErrorMessage] = useState<string>("");
	const history = useHistory();

	const dispatch = useDispatch();
	const { user } = useSelector(( state : RootState ) => ({
		user: state.auth.email
	}));

	const handleInputId = (e: React.ChangeEvent<HTMLInputElement>) => {
		const { value } = e.target;
		setInputId(value);
		dispatch(
			changeField({
				email: value,
				password: inputPw,
			})
		);
	};

	const handleInputPw = (e: React.ChangeEvent<HTMLInputElement>) => {
		const { value } = e.target;
		setInputPw(value);
		dispatch(
			changeField({
				email: inputId,
				password: value,
			})
		);
	};

	const showErrorModal = (errorMsg: string) => {
		return (
			<ErrorModal setModalOpen={setErrorModal} errorMessage={errorMsg} />
		);
	};
	
	// 컴포넌트가 처음 렌더링될 때 form 을 초기화
	useEffect(() => {
		dispatch(initializeForm());
	}, [dispatch]);

	const onClickLogin = () => {
		console.log("click login");
		console.log("ID : ", inputId);
		console.log("Pw : ", inputPw);

		// hook call 에러 뜸 (handler 안에 useEffect 사용할 시)
		axios
			.post(`${LOGIN_URL}/login`, {
				email: inputId,
				password: inputPw,
			})
			.then((res) => {
				console.log(res);
				const { accessToken } = res.data.data;
				axios.defaults.headers.common.Authorization = accessToken
					? `${accessToken}`
					: "";
				console.log(accessToken);

				// loaclStorage에 저장
				try {
					localStorage.setItem("email", JSON.stringify(user));
					dispatch(tempSetUser(inputId));
					setAccessToken(accessToken);
				} catch (e) {
					console.log("Login login is not working");
				}

				// accessToken 만료하기 1분 전에 로그인 연장
				setTimeout(onSlientRefresh, JWT_EXPIREY_TIME - 60000);
				history.push("/");
			})
			.catch((e) => {
				const { status } = e.response;
				const { reason } = e.response.data;
				console.log(reason);
				console.log(status);
				if (status === 409) {
					if (reason === "Login Email Wrong") {
						setErrorModal(true);
						setErrorMessage("아이디가 틀렸습니다.");
					} else if (reason === "Login Password Wrong") {
						setErrorModal(true);
						setErrorMessage("비밀번호가 틀렸습니다.");
					} else
						console.log("원인을 알 수 없는 에러가 발생하였습니다.");
				}
				if(status === 400) {
					setErrorModal(true);
					setErrorMessage("로그인 형식 오류 (ID : 이메일, PW : 8자 이상)");
				}
				dispatch(initializeForm());
			});
	};

	const onSlientRefresh = () => {
		axios
			.post("/slient-refresh", {
				email: inputId,
				password: inputPw,
			})
			.then((res) => {
				console.log(res);
				console.log("로그인 성공");
			})
			.catch((e) => {
				console.log("실패");
				console.error(e);
			});
	};

	return (
		<Wrapper>
			<Container>
				<TitleCtn>
					<h4>로그인</h4>
					<img src="image/Line 13.png" alt="" />
					<h4>SIGN IN</h4>
				</TitleCtn>
				<InputCtn>
					<input
						className="id input"
						type="text"
						name="input_id"
						id="login_id"
						value={inputId}
						onChange={handleInputId}
						placeholder="아이디를 입력하세요."
					/>
					<input
						className="password input"
						type="password"
						name="input_pw"
						id="login_pw"
						value={inputPw}
						onChange={handleInputPw}
						placeholder="비밀번호를 입력하세요."
					/>
				</InputCtn>
				<BtnCtn>
					<button
						type="button"
						className="btn login-btn"
						onClick={onClickLogin}
					>
						로그인
					</button>
					<Link to="/register">
						<button type="button" className="btn register-btn">
							회원가입
						</button>
					</Link>
				</BtnCtn>
			</Container>
			{!!errorModal && showErrorModal(errorMessage)}
		</Wrapper>
	);
};

const Wrapper = styled.div`
	margin-top: 20px;
	border: 1px solid #c4c4c4;
	border-radius: 2px;
	padding: 10px;
	background-color: white;
	width: 400px;
	height: 490px;
`;

const Container = styled.div`
	display: flex;
	align-items: center;
	flex-direction: column;
`;

const TitleCtn = styled.div`
	font-weight: bold;
	color: #646464;
	margin-top: 40px;
	width: 300px;
	display: flex;
	justify-content: space-evenly;
	font-size: 20px;

	img {
		width: 100px;
		height: 3px;
		margin-top: 14px;
	}
`;

const InputCtn = styled.div`
	height: 230px;
	width: 298px;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-direction: column;

	input {
		font-size: 16px;
		font-weight: bold;
		width: 280px;
		height: 45px;
		margin: 10px;
		padding-left: 20px;
		border: 1px solid #c9cbca;
	}
	input-row {
		width: 500px;
		margin: 0;
		padding: 0;
		display: inline-block;
	}
`;

const BtnCtn = styled.div`
	margin-top: -10px;
	button {
		font-size: 16px;
		font-weight: bold;
		color: #ffffff;
		border: none;
		margin: 8px;
	}
	button.btn {
		width: 241px;
		height: 53px;
		border-radius: 12px;
	}
	button.login-btn {
		background-color: #3171543d;
	}

	button.register-btn {
		margin-top: 10px;
		background-color: #54b689;
		color: #fff;
	}

	button:hover {
		background-color: #fff;
		color: #54b689;
		border: 2px solid #54b689;
	}
`;

export default Login;
