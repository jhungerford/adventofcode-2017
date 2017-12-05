package main

import "testing"

func checkPassphraseIsValid(t *testing.T, passphrase string, expected bool) {
	actual := PasspharaseIsValid(passphrase)

	if expected != actual {
		t.Errorf("Wrong valid value for passphrase %s.  Expected: %t, Actual: %t", passphrase, expected, actual)
	}
}

func TestPasspharaseIsValid(t *testing.T) {
	checkPassphraseIsValid(t, "aa bb cc dd ee", true)
	checkPassphraseIsValid(t, "aa bb cc dd aa", false)
	checkPassphraseIsValid(t, "aa bb cc dd aaa", true)
}
