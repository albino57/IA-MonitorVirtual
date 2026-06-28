import styles from './DefaultLayout.module.css'

    function DefaultLayout({ children }) {
      return (
        <div className={styles.container}>
          <header className={styles.header}>
            <h1>Monitor Virtual IA</h1>
          </header>
          <main className={styles.main}>
            {children}
          </main>
        </div>
      )
    }

export default DefaultLayout