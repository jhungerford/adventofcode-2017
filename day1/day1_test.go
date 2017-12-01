package main

import (
	"testing"
)

func checkStringToNumbers(t *testing.T, s string, expected []int) {
	actual := stringToNumbers(s)

	if len(actual) != len(expected) {
		t.Errorf("Actual and expected had different lengths.  actual: %v, expected: %v", actual, expected)
	}

	for i, v := range actual {
		if v != expected[i] {
			t.Errorf("Actual and expected had different contents.  actual: %v, expected: %v", actual, expected)
		}
	}
}

func TestStringToNumbers(t *testing.T) {
	checkStringToNumbers(t, "1122", []int{1, 1, 2, 2})
	checkStringToNumbers(t, "1111", []int{1, 1, 1, 1})
	checkStringToNumbers(t, "1234", []int{1, 2, 3, 4})
	checkStringToNumbers(t, "91212129", []int{9, 1, 2, 1, 2, 1, 2, 9})
}

func checkCaptcha(t *testing.T, s string, expected int) {
	actual := captcha(stringToNumbers(s))

	if expected != actual {
		t.Errorf("Wrong captcha for '%s'.  actual: %d, expected: %d", s, actual, expected)
	}
}

func TestCaptcha(t *testing.T) {
	checkCaptcha(t, "1122", 3)
	checkCaptcha(t, "1111", 4)
	checkCaptcha(t, "1234", 0)
	checkCaptcha(t, "91212129", 9)
}

func checkCaptchaHalfway(t *testing.T, s string, expected int) {
	actual := captchaHalfway(stringToNumbers(s))

	if expected != actual {
		t.Errorf("Wrong halfway captcha for '%s'.  actual: %d, expected: %d", s, actual, expected)
	}
}

func TestCaptchaHalfway(t *testing.T) {
	checkCaptchaHalfway(t, "1212", 6)
	checkCaptchaHalfway(t, "1221", 0)
	checkCaptchaHalfway(t, "12345", 0)
	checkCaptchaHalfway(t, "123123", 12)
	checkCaptchaHalfway(t, "12131415", 4)
}