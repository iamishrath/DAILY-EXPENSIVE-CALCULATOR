package com.example.dailyexpensivecalculator;

public class TransactionClass {
        private int amount;
        private String message;
        private boolean positive;

        public TransactionClass(int amount, String message, boolean positive) {
            this.amount = amount;
            this.message = message;
            this.positive = positive;
        }

        public int getAmount() {
            return amount;
        }

    public void setAmount() {
        setAmount();
    }

    public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isPositive() {
            return positive;
        }

        public void setPositive(boolean positive) {
            this.positive = positive;
        }
    }


