package com.github.winchopper.pd.book;

import com.github.winchopper.pd.crypto.EncryptorSalt;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

@XmlRootElement
@XmlAccessorType(FIELD)
public class Book {

    @XmlElement(name = "salt")
    @XmlJavaTypeAdapter(EncryptorSalt.XmlJavaTypeAdapter.class)
    private EncryptorSalt salt;

    @XmlElementWrapper(name = "pages")
    @XmlElement(name = "page")
    private List<Page> pages = new ArrayList<>();

}
