package com.github.windchopper.tools.password.drop.book

import com.github.windchopper.tools.password.drop.crypto.EncryptEngine
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import javax.enterprise.context.ApplicationScoped
import javax.xml.bind.JAXBContext

@ApplicationScoped class BookCase {

    private val bindingContext: JAXBContext = JAXBContext.newInstance(Book::class.java)

    fun decryptBook(book: Book, password: String): Book {
        with (book.salt) {
            return when (this) {
                null -> book
                else -> {
                    val encryptEngine = EncryptEngine(password, this)
                    book.copy { it?.let(encryptEngine::decrypt) }
                }
            }
        }
    }

    fun readBook(bookPath: Path): Book {
        Files.newBufferedReader(bookPath).use {
            return (bindingContext.createUnmarshaller().unmarshal(it) as Book)
                .also { it.path = bookPath }
        }
    }

    fun encryptBook(book: Book, password: String): Book {
        with (book.salt) {
            return when (this) {
                null -> book
                else -> {
                    val encryptEngine = EncryptEngine(password, this)
                    book.copy { it?.let(encryptEngine::encrypt) }
                }
            }
        }
    }

    fun saveBook(book: Book, bookPath: Path) {
        val temporaryBookPath = Files.createTempFile("password-drop-book-", "")

        try {
            Files.newBufferedWriter(temporaryBookPath).use {
                bindingContext.createMarshaller().marshal(book, it)
            }

            book.path = Files.move(temporaryBookPath, bookPath,
                StandardCopyOption.REPLACE_EXISTING)
        } finally {
            Files.delete(temporaryBookPath)
        }
    }

}