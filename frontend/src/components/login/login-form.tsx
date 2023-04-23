import React, { FC, ReactElement } from "react";
import { Formik } from "formik";
import * as Yup from "yup";
import YupPassword from "yup-password";
import {
  Box,
  StyledForm,
  InputField,
  LogoImage,
  Button,
  H3,
  LoginLink,
  ErrorMessage,
} from "../registration/registration-form";

YupPassword(Yup);

type ChildProps = {};

const SignupSchema = Yup.object().shape({
  email: Yup.string()
    .email("Invalid email")
    .matches(/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/i, "Invalid email format.")
    .required("Required"),
  password: Yup.string()
    .min(8, "Must be at least 8 charakters long.")
    .minLowercase(1, "Must contain at least one lowercase charakter.")
    .minUppercase(1, "Must contain at least one uppercase charakter.")
    .minNumbers(1, "Must contain at least one number.")
    .minSymbols(1, "Must contain at least special charakter.")
    .required("Required"),
});

const LoginForm: FC<ChildProps> = (): ReactElement => {
  const submitHandler = (values: { email: string; password: string }) => {
    console.log(values);
  };

  return (
    <Box>
      <LogoImage
        src="logo.svg"
        alt="Logo of the page"
        width={60}
        height={60}
        priority
      />
      <Formik
        initialValues={{ email: "", password: "" }}
        validationSchema={SignupSchema}
        onSubmit={submitHandler}
      >
        {({ errors, touched }) => (
          <StyledForm>
            <InputField name="email" placeholder="Address Email" />
            {errors.email && touched.email && (
              <ErrorMessage>{errors.email}</ErrorMessage>
            )}
            <InputField
              name="password"
              type="password"
              placeholder="Password"
            />
            {errors.password && touched.password && (
              <ErrorMessage>{errors.password}</ErrorMessage>
            )}
            <Button type="submit">Log In</Button>
          </StyledForm>
        )}
      </Formik>
      <H3>
        Need a new account?
        <LoginLink href={"/registration"}>Register Now</LoginLink>
      </H3>
    </Box>
  );
};

export default LoginForm;