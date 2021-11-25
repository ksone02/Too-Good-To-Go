/* eslint-disable react/jsx-props-no-spreading */
import React from "react";
import styled from "@emotion/styled/macro";

const Select = [
	{ name: "주문자 정보와 동일", hex: "#ffb598" },
	{ name: "새로운 배송지", hex: "#c2dbff" },
];

const ColorSelectorContainer = styled.div`
	display: flex;
	justify-content: flex-start;
	width: 95%;
	margin: 2em 1em 29px 1em;
	border-bottom: 1px solid #eee;
	padding-bottom: 1em;
`;

const Label = styled.label`
	display: inline-block;
	margin-top: 4px;
	margin-right: 13px;
	padding: 7px;
`;

const RadioButton = styled.input`
	vertical-align: middle;
	width: 1.27em;
	height: 1.27em;
	background: red;
	&:checked + ${Label} {
		color: no-repeat ${(props) => props.color};
	}
`;

const SelectInfo: React.FC = () => {
	return (
		<>
			<ColorSelectorContainer>
				{Select.map((row) => (
					<div key={row.name}>
						<RadioButton
							id={row.name}
							type="radio"
							name="select-info"
							value={row.name}
						/>
						<Label htmlFor={row.name} color={row.hex}>
							{row.name}
						</Label>
					</div>
				))}
			</ColorSelectorContainer>
		</>
	);
};
export default SelectInfo;
