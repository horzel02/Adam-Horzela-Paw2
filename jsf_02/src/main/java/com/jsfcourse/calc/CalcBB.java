package com.jsfcourse.calc;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

@Named
@RequestScoped
public class CalcBB {

    private String loanAmount; // Kwota kredytu
    private String years;      // Liczba lat
    private String interestRate; // Oprocentowanie
    private String result;      // Wynik 

    @Inject
    FacesContext ctx;

    // Gettery i settery
    public String getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getYears() {
        return years;
    }

    public void setYears(String years) {
        this.years = years;
    }

    public String getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    private boolean doTheMath() {
        try {
            double loanAmountVal = Double.parseDouble(loanAmount);
            int yearsVal = Integer.parseInt(years);
            double interestRateVal = Double.parseDouble(interestRate) / 100;

            if (loanAmountVal < 1000 || loanAmountVal > 1000000) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kwota kredytu musi być w zakresie od 1000 do 1000000.", null));
                return false;
            }
            if (yearsVal < 1 || yearsVal > 30) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Liczba lat musi być w zakresie od 1 do 30.", null));
                return false;
            }
            if (interestRateVal < 0.001 || interestRateVal > 0.20) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Oprocentowanie musi być w zakresie od 0.1% do 20%.", null));
                return false;
            }
            
            int months = yearsVal * 12;
            double monthlyRate = interestRateVal / 12;

            if (interestRateVal > 0) {
                double monthlyPayment = (loanAmountVal * monthlyRate * Math.pow(1 + monthlyRate, months))
                        / (Math.pow(1 + monthlyRate, months) - 1);
                result = String.format("%.2f", monthlyPayment);
            } else {
                result = String.format("%.2f", loanAmountVal / months);
            }

            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operacja wykonana poprawnie", null));
            return true;
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd danych. Wprowadź poprawne wartości liczbowe.", null));
            return false;
        }
    }

    // Go to "showresult" if ok
    public String calc() {
        if (doTheMath()) {
            return "showresult";
        }
        return null;
    }

    // Put result in messages on AJAX call
    public String calc_AJAX() {
        if (doTheMath()) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Miesięczna kwota: " + result, null));
        }
        return null;
    }

    public String info() {
        return "info";
    }
}
