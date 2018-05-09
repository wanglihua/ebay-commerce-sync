package ozosync.service;

import org.springframework.stereotype.Service;

@Service
public class AddressFormatService {

    public String normalizePhone(String phone) {
        if (phone != null && (!phone.equals(""))) {
            phone = phone.replace(" ", "");

            if (phone.length() > 4) {
                return phone.substring(0, 4) + " " + phone.substring(4);
            }
        }

        return phone;
    }

    public String nullToEmpty(String str) {
        if (str == null) {
            str = "";
        }

        return str;
    }
}
