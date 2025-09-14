from PIL import Image

# Função para revelar mensagem
def decode_image(image_path):
    img = Image.open(image_path)
    width, height = img.size
    binary_data = ""

    for row in range(height):
        for col in range(width):
            r, g, b = img.getpixel((col, row))
            binary_data += str(b & 1)

    # dividir em blocos de 8 bits
    all_bytes = [binary_data[i:i+8] for i in range(0, len(binary_data), 8)]
    message = ""
    for byte in all_bytes:
        char = chr(int(byte, 2))
        if message.endswith("~~~~"):  # delimitador
            break
        message += char
    return message[:-4]  # remover delimitador


# ----------------------------
# USO:
# ----------------------------

# Revelar
mensagem = decode_image("imagem_com_mensagem.png")
print("Mensagem escondida:", mensagem)

