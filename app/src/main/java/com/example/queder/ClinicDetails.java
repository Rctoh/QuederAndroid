package com.example.queder;

public class ClinicDetails {

    private String clinicId;
    private String imageUrl;
    private String clinicName;
    private String address;
    private String town;
    private String openingHours;
    private String phoneNumber;
    private String doctor;
    private String clinicType;
    private String clinicProgramme;
    private String paymentMethod;
    private String publicTransport;
    private String carpark;
    private String price;
    private String rating;
    private String currentQueue;
    private String queueDetails;
    private String lastQueueNumber;

    private String doctorPublicKey;

    public ClinicDetails(String clinicId, String imageUrl, String clinicName, String address, String town,
                         String openingHours, String phoneNumber, String doctor, String clinicType, String clinicProgramme,
                         String paymentMethod, String publicTransport, String carpark, String price, String rating,
                         String currentQueue, String queueDetails, String lastQueueNumber, String doctorPublicKey) {
        this.clinicId = clinicId;
        this.imageUrl = imageUrl;
        this.clinicName = clinicName;
        this.address = address;
        this.town = town;
        this.openingHours = openingHours;
        this.phoneNumber = phoneNumber;
        this.doctor = doctor;
        this.clinicType = clinicType;
        this.clinicProgramme = clinicProgramme;
        this.paymentMethod = paymentMethod;
        this.publicTransport = publicTransport;
        this.carpark = carpark;
        this.price = price;
        this.rating = rating;
        this.currentQueue = currentQueue;
        this.queueDetails = queueDetails;
        this.lastQueueNumber = lastQueueNumber;
        this.doctorPublicKey = doctorPublicKey;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getClinicType() {
        return clinicType;
    }

    public void setClinicType(String clinicType) {
        this.clinicType = clinicType;
    }

    public String getClinicProgramme() {
        return clinicProgramme;
    }

    public void setClinicProgramme(String clinicProgramme) {
        this.clinicProgramme = clinicProgramme;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPublicTransport() {
        return publicTransport;
    }

    public void setPublicTransport(String publicTransport) {
        this.publicTransport = publicTransport;
    }

    public String getCarpark() {
        return carpark;
    }

    public void setCarpark(String carpark) {
        this.carpark = carpark;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCurrentQueue() {
        return currentQueue;
    }

    public void setCurrentQueue(String currentQueue) {
        this.currentQueue = currentQueue;
    }

    public String getQueueDetails() {
        return queueDetails;
    }

    public void setQueueDetails(String queueDetails) {
        this.queueDetails = queueDetails;
    }

    public String getLastQueueNumber() {
        return lastQueueNumber;
    }

    public void setLastQueueNumber(String lastQueueNumber) {
        this.lastQueueNumber = lastQueueNumber;
    }

    public String getDoctorPublicKey() {
        return doctorPublicKey;
    }

    public void setDoctorPublicKey(String doctorPublicKey) {
        this.doctorPublicKey = doctorPublicKey;
    }

    @Override
    public String toString() {
        return "ClinicDetails{" +
                "id='" + clinicId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", clinicName='" + clinicName + '\'' +
                ", address='" + address + '\'' +
                ", town='" + town + '\'' +
                ", openingHours='" + openingHours + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", doctor='" + doctor + '\'' +
                ", clinicType='" + clinicType + '\'' +
                ", clinicProgramme='" + clinicProgramme + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", publicTransport='" + publicTransport + '\'' +
                ", carpark='" + carpark + '\'' +
                ", price='" + price + '\'' +
                ", rating='" + rating + '\'' +
                ", currentQueue='" + currentQueue + '\'' +
                ", queueDetails='" + queueDetails + '\'' +
                ", lastQueueNumber='" + lastQueueNumber + '\'' +
                '}';
    }
}
